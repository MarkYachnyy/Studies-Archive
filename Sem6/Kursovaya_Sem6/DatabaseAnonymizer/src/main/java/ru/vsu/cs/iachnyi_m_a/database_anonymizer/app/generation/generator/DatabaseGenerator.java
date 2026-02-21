package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator;

import org.postgresql.util.PSQLException;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.GlobalTimeMeasurer;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.discrete.DiscreteDistribution;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.discrete.DiscreteDistributionFactory;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.constraint.Unique;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.DiscreteDistributionType;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.schema.Column;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.schema.DatabaseSchema;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.schema.Table;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.schema.ValueType;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.constraint.ConstraintSet;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.constraint.ForeignKey;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.constraint.PrimaryKey;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.Rule;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.RuleSet;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.primary_key_generator.PrimaryKeyGeneratorFactory;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.graph.RelationMapElement;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.graph.TableRelationGraph;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.graph.TableRelationGraphNode;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.query.QueryExecutor;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.query.QueryTool;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.ColumnGenerator;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class DatabaseGenerator {

    private List<TableGenerator> tableGenerators;
    private TableRelationGraph tableRelationGraph;
    private final QueryTool queryTool;

    public DatabaseGenerator(QueryTool queryTool) {
        this.queryTool = queryTool;
    }

    public void fillDatabase(DatabaseSchema schema, ConstraintSet constraintSet, RuleSet ruleSet, String firstTableName, int firstTableCount) throws SQLException {

        GlobalTimeMeasurer.setOnFinishedLambda(() -> {
            try {
                QueryExecutor executor = queryTool.getQueryExecutor();
                for(PrimaryKey key: constraintSet.getPrimaryKeys()) {
                    executor.executeQuery(createPrimaryKeyConstraintQuery(key.getTableName(), key.getColumnName()));
                }
                for (ForeignKey key: constraintSet.getForeignKeys()) {
                    executor.executeQuery(createForeignKeyConstraintQuery(key.getSourceTableName(), key.getSourceColumnName(), key.getTargetTableName(), key.getTargetColumnName()));
                }
                executor.commit();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
        //СОЗДАНИЕ ТАБЛИЦ

        List<Table> tables = schema.getTables();
        tableGenerators = new ArrayList<>();
        QueryExecutor executor = queryTool.getQueryExecutor();
        for (Table table : tables) {
            try {
                executor.executeQuery("DROP TABLE " + table.getName() + " CASCADE;");
            } catch (PSQLException e) {

            }
            executor.executeQuery(getTableCreationQuery(table));
            tableGenerators.add(new TableGenerator(table.getName()));
        }

        for (Unique unique : constraintSet.getUniques()) {
            executor.executeQuery(createUniqueConstraintQuery(unique.getTableName(), unique.getColumnName()));
        }

        //ЗАПОЛНЕНИЕ ГРАФА ОТНОШЕНИЯ ТАБЛИЦ

        tableRelationGraph = getTableRelationGraph(schema.getTables().stream().map(Table::getName).toList(), constraintSet.getForeignKeys());

        //ДОБАВЛЕНИЕ ГЕНЕРАТОРОВ ПЕРВИЧНЫХ КЛЮЧЕЙ

        for (PrimaryKey primaryKey : constraintSet.getPrimaryKeys()) {
            Table table = tables.stream().filter(table1 -> table1.getName().equals(primaryKey.getTableName())).findFirst().get();
            Column column = table.getColumns().stream().filter(c -> c.getName().equals(primaryKey.getColumnName())).findFirst().get();
            tableGenerators.stream()
                    .filter(t -> t.getTableName().equals(primaryKey.getTableName()))
                    .findFirst()
                    .get()
                    .setPrimaryKeyGenerator(PrimaryKeyGeneratorFactory.createColumnGenerator(primaryKey.getColumnName(), column.getType()));
        }

        //ДОБАВЛЕНИЕ ГЕНЕРАТОРОВ ОБЫЧНЫХ ЗНАЧЕНИЙ

        List<Rule> allRules = new ArrayList<>();
        if (ruleSet.getBooleanRules() != null) allRules.addAll(ruleSet.getBooleanRules());
        if (ruleSet.getIntegerRules() != null) allRules.addAll(ruleSet.getIntegerRules());
        if (ruleSet.getFloatRules() != null) allRules.addAll(ruleSet.getFloatRules());
        if (ruleSet.getStringRules() != null) allRules.addAll(ruleSet.getStringRules());
        if (ruleSet.getDateRules() != null) allRules.addAll(ruleSet.getDateRules());
        if (ruleSet.getDateIntervalRules() != null) allRules.addAll(ruleSet.getDateIntervalRules());
        if (ruleSet.getFloatIntervalRules() != null) allRules.addAll(ruleSet.getFloatIntervalRules());

        for (Rule rule : allRules) {
            ColumnGenerator generator = rule.toGenerator(constraintSet.getUniques().stream().anyMatch(u -> u.getColumnName().equals(rule.getColumnName()) && u.getTableName().equals(rule.getTableName())));
            TableGenerator tableGenerator = tableGenerators.stream().filter(table1 -> table1.getTableName().equals(rule.getTableName())).findFirst().get();
            tableGenerator.getColumnGenerators().add(generator);
            if (generator.getNullChance() == 0f) {
                for (String columnName : generator.getColumnNames()) {
                    executor.executeQuery(createNotNullConstraintQuery(tableGenerator.getTableName(), columnName));
                }
            }
        }

        executor.commit();

        //ЗАПУСК ЗАПОЛНЕНИЯ ТАБЛИЦ

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1, 2, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        threadPoolExecutor.execute(()  -> {
            try {
                fillFirstTable(tableRelationGraph.findNodeByTableName(firstTableName),
                        threadPoolExecutor,
                        queryTool,
                        tableGenerators.stream().filter(tg -> tg.getTableName().equals(firstTableName)).findFirst().get(),
                        firstTableCount);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private TableRelationGraph getTableRelationGraph(List<String> allTables, List<ForeignKey> foreignKeys) {
        TableRelationGraph graph = new TableRelationGraph();
        for (String tableName : allTables) {
            graph.getNodes().add(new TableRelationGraphNode(tableName));
        }
        for (ForeignKey foreignKey : foreignKeys) {
            TableRelationGraphNode sourceNode = graph.findNodeByTableName(foreignKey.getSourceTableName());
            TableRelationGraphNode targetNode = graph.findNodeByTableName(foreignKey.getTargetTableName());
            sourceNode.getChildren().add(new RelationMapElement(
                    foreignKey.getSourceColumnName(),
                    targetNode,
                    DiscreteDistributionFactory.createDiscreteDistribution(DiscreteDistributionType.valueOf(foreignKey.getSourceDistributionType()), foreignKey.getSourceDistributionParams()),
                    foreignKey.getSourceZeroChance(),
                    foreignKey.getTargetZeroChance()));
            targetNode.getParents().add(new RelationMapElement(
                    foreignKey.getSourceColumnName(),
                    sourceNode,
                    DiscreteDistributionFactory.createDiscreteDistribution(DiscreteDistributionType.valueOf(foreignKey.getSourceDistributionType()), foreignKey.getSourceDistributionParams()),
                    foreignKey.getSourceZeroChance(),
                    foreignKey.getTargetZeroChance()));
        }
        return graph;
    }


    private void fillFirstTable(TableRelationGraphNode graphNode,
                                ThreadPoolExecutor threadExecutor,
                                QueryTool queryTool,
                                TableGenerator tableGenerator,
                                int count) throws SQLException {
        //ГЕНЕРАЦИЯ НАБОРА ПК

        List<String> generatedKeys = IntStream.range(0, count).mapToObj(i -> tableGenerator.getPrimaryKeyGenerator().nextValue()).toList();

        //ЗАПУСК ЗАПОЛНЕНИЯ РОДИТЕЛЬСКИХ ТАБЛИЦ

        for (RelationMapElement element : graphNode.getParents()) {
            String fkName = element.getForeignKeyName();
            TableRelationGraphNode node = element.getNode();
            threadExecutor.execute(() -> {
                try {
                    fillParentTable(node,
                            threadExecutor,
                            queryTool,
                            tableGenerators.stream().filter(tg -> tg.getTableName().equals(node.getTableName())).findFirst().get(),
                            tableGenerator.getTableName(),
                            tableGenerator.getPrimaryKeyGenerator().getColumnName(),
                            reduceRandomly(generatedKeys, element.getSourceZeroChance()),
                            fkName,
                            element.getDistribution(),
                            element.getTargetZeroChance());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        //ЗАПОЛНЕНИЕ СЕБЯ

        System.out.println("FILLING TABLE " + tableGenerator.getTableName());
        QueryExecutor queryExecutor = queryTool.getQueryExecutor();
//        queryExecutor.executeQuery(
//                createPrimaryKeyConstraintQuery(tableGenerator.getTableName(),
//                        tableGenerator.getPrimaryKeyGenerator().getColumnName()));
        for (int i = 0; i < count; i++) {
            List<String> columnNames = new ArrayList<>();
            List<String> values = new ArrayList<>();
            columnNames.add(tableGenerator.getPrimaryKeyGenerator().getColumnName());
            values.add(generatedKeys.get(i));
            for (ColumnGenerator columnGenerator : tableGenerator.getColumnGenerators()) {
                columnNames.addAll(Arrays.stream(columnGenerator.getColumnNames()).toList());
                values.addAll(Arrays.stream(columnGenerator.getNextValues()).toList());
            }
            queryExecutor.executeQuery(createInsertQuery(tableGenerator.getTableName(), columnNames, values));
        }
        queryExecutor.commit();
        System.out.println("FILLED TABLE " + tableGenerator.getTableName() + " WITH " + generatedKeys.size() + " ROWS");


        //ЗАПУСК ЗАПОЛНЕНИЯ ДОЧЕРНИХ ТАБЛИЦ

        for (RelationMapElement element : graphNode.getChildren()) {
            threadExecutor.execute(() -> {
                TableRelationGraphNode node = element.getNode();
                try {
                    fillChildTable(element.getNode(),
                            threadExecutor,
                            queryTool,
                            tableGenerators.stream().filter(tg -> tg.getTableName().equals(node.getTableName())).findFirst().get(),
                            tableGenerator.getTableName(),
                            reduceRandomly(generatedKeys, element.getTargetZeroChance()),
                            tableGenerator.getPrimaryKeyGenerator().getColumnName(),
                            element.getForeignKeyName(),
                            element.getDistribution(),
                            element.getSourceZeroChance());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

        }

        GlobalTimeMeasurer.tick();
    }

    private void fillChildTable(TableRelationGraphNode graphNode,
                                ThreadPoolExecutor threadExecutor,
                                QueryTool queryTool,
                                TableGenerator tableGenerator,
                                String parentTableName,
                                List<String> parentPrimaryKeyValues,
                                String parentPrimaryKeyName,
                                String parentForeignKeyName,
                                DiscreteDistribution distribution,
                                float sourceZeroChance) throws SQLException {

        List<String> generatedNonNullKeys = new ArrayList<>();
        List<String> generatedNullKeys = new ArrayList<>();
        List<Integer> keysMap = new ArrayList<>();

        //ГЕНЕРАЦИЯ НАБОРА ПЕРВИЧНЫХ КЛЮЧЕЙ

        int pkLeft = parentPrimaryKeyValues.size();
        while (pkLeft >= 0) {
            int count = distribution.next();
            pkLeft -= count;
            keysMap.add(count);
            generatedNonNullKeys.add(tableGenerator.getPrimaryKeyGenerator().nextValue());
        }
        keysMap.removeLast();
        generatedNonNullKeys.removeLast();
        float targetZeroRatio = sourceZeroChance / (1 - sourceZeroChance);
        for (int i = 0; i < (int) (generatedNonNullKeys.size() * targetZeroRatio); i++) {
            generatedNullKeys.add(tableGenerator.getPrimaryKeyGenerator().nextValue());
        }
        List<String> allGeneratedKeys = new ArrayList<>(generatedNonNullKeys);
        allGeneratedKeys.addAll(generatedNullKeys);

        //ЗАПУСК ЗАПОЛНЕНИЯ РОДИТЕЛЬСКИХ ТАБЛИЦ

        for (RelationMapElement element : graphNode.getParents()) {
            String fkName = element.getForeignKeyName();
            TableRelationGraphNode node = element.getNode();
            if (node.getTableName().equals(parentTableName)) continue;
            threadExecutor.execute(() -> {
                try {
                    fillParentTable(node,
                            threadExecutor,
                            queryTool,
                            tableGenerators.stream().filter(tg -> tg.getTableName().equals(node.getTableName())).findFirst().get(),
                            tableGenerator.getTableName(),
                            tableGenerator.getPrimaryKeyGenerator().getColumnName(),
                            reduceRandomly(allGeneratedKeys, element.getSourceZeroChance()),
                            fkName,
                            element.getDistribution(),
                            element.getTargetZeroChance());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        //ЗАПОЛНЕНИЕ СЕБЯ + ОБНОВЛЕНИЕ РОДИТЕЛЬСКОЙ ТАБЛИЦЫ

        System.out.println("FILLING TABLE " + tableGenerator.getTableName());

        QueryExecutor queryExecutor = queryTool.getQueryExecutor();
//        queryExecutor.executeQuery(
//                createPrimaryKeyConstraintQuery(tableGenerator.getTableName(),
//                        tableGenerator.getPrimaryKeyGenerator().getColumnName()));
//
//        queryExecutor.executeQuery(createForeignKeyConstraintQuery(
//                parentTableName,
//                parentForeignKeyName,
//                tableGenerator.getTableName(),
//                tableGenerator.getPrimaryKeyGenerator().getColumnName()));

        int updatedRows = 0;
        for (int i = 0; i < keysMap.size(); i++) {
            List<String> columnNames = new ArrayList<>();
            List<String> values = new ArrayList<>();
            columnNames.add(tableGenerator.getPrimaryKeyGenerator().getColumnName());
            values.add(generatedNonNullKeys.get(i));
            for (ColumnGenerator columnGenerator : tableGenerator.getColumnGenerators()) {
                columnNames.addAll(Arrays.stream(columnGenerator.getColumnNames()).toList());
                values.addAll(Arrays.stream(columnGenerator.getNextValues()).toList());
            }
            queryExecutor.executeQuery(createInsertQuery(tableGenerator.getTableName(), columnNames, values));
            for (int j = updatedRows; j < updatedRows + keysMap.get(i); j++) {
                queryExecutor.executeQuery(createUpdateQuery(parentTableName, parentPrimaryKeyName, parentPrimaryKeyValues.get(j), parentForeignKeyName, generatedNonNullKeys.get(i)));
            }
            updatedRows += keysMap.get(i);
        }

        for (String nullKey : generatedNullKeys) {
            List<String> columnNames = new ArrayList<>();
            List<String> values = new ArrayList<>();
            columnNames.add(tableGenerator.getPrimaryKeyGenerator().getColumnName());
            values.add(nullKey);
            for (ColumnGenerator columnGenerator : tableGenerator.getColumnGenerators()) {
                columnNames.addAll(Arrays.stream(columnGenerator.getColumnNames()).toList());
                values.addAll(Arrays.stream(columnGenerator.getNextValues()).toList());
            }
            queryExecutor.executeQuery(createInsertQuery(tableGenerator.getTableName(), columnNames, values));
        }

        queryExecutor.commit();


        System.out.println("FILLED TABLE " + tableGenerator.getTableName() + " WITH " + allGeneratedKeys.size() + " ROWS");


        //ЗАПУСК ЗАПОЛНЕНИЯ ДОЧЕРНИХ ТАБЛИЦ

        for (RelationMapElement element : graphNode.getChildren()) {
            threadExecutor.execute(() -> {
                TableRelationGraphNode node = element.getNode();
                try {
                    fillChildTable(element.getNode(),
                            threadExecutor,
                            queryTool,
                            tableGenerators.stream().filter(tg -> tg.getTableName().equals(node.getTableName())).findFirst().get(),
                            tableGenerator.getTableName(),
                            reduceRandomly(allGeneratedKeys, element.getTargetZeroChance()),
                            tableGenerator.getPrimaryKeyGenerator().getColumnName(),
                            element.getForeignKeyName(),
                            element.getDistribution(),
                            element.getSourceZeroChance()
                    );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

        }

        GlobalTimeMeasurer.tick();

    }

    private void fillParentTable(TableRelationGraphNode graphNode,
                                 ThreadPoolExecutor executor,
                                 QueryTool queryTool,
                                 TableGenerator tableGenerator,
                                 String childTableName,
                                 String childPrimaryKeyName,
                                 List<String> childPrimaryKeyValues,
                                 String parentForeignKeyName,
                                 DiscreteDistribution distribution,
                                 float targetZeroChance) throws SQLException {
        List<String> generatedNonNullKeys = new ArrayList<>();
        List<String> generatedNullKeys = new ArrayList<>();
        List<Integer> keysMap = new ArrayList<>();
        float ratio = targetZeroChance / (1 - targetZeroChance);
        for (int j = 0; j < childPrimaryKeyValues.size(); j++) {
            int count = distribution.next();
            for (int i = 0; i < count; i++) {
                generatedNonNullKeys.add(tableGenerator.getPrimaryKeyGenerator().nextValue());
            }
            keysMap.add(count);
        }
        for (int i = 0; i < (int) (generatedNonNullKeys.size() * ratio); i++) {
            generatedNullKeys.add(tableGenerator.getPrimaryKeyGenerator().nextValue());
        }
        List<String> allGeneratedKeys = new ArrayList<>(generatedNonNullKeys);
        allGeneratedKeys.addAll(generatedNullKeys);
        for (RelationMapElement element : graphNode.getParents()) {
            String fkName = element.getForeignKeyName();
            TableRelationGraphNode node = element.getNode();
            executor.execute(() -> {
                try {
                    fillParentTable(node,
                            executor,
                            queryTool,
                            tableGenerators.stream().filter(tg -> tg.getTableName().equals(node.getTableName())).findFirst().get(),
                            tableGenerator.getTableName(),
                            tableGenerator.getPrimaryKeyGenerator().getColumnName(),
                            reduceRandomly(allGeneratedKeys, element.getSourceZeroChance()),
                            fkName,
                            element.getDistribution(),
                            element.getTargetZeroChance());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        System.out.println("FILLING TABLE " + tableGenerator.getTableName());

        QueryExecutor queryExecutor = queryTool.getQueryExecutor();

//        queryExecutor.executeQuery(
//                createPrimaryKeyConstraintQuery(tableGenerator.getTableName(),
//                        tableGenerator.getPrimaryKeyGenerator().getColumnName()));
//        queryExecutor.executeQuery(createForeignKeyConstraintQuery(
//                tableGenerator.getTableName(),
//                parentForeignKeyName,
//                childTableName,
//                childPrimaryKeyName));

        int sum = 0;
        for (int countNumber = 0; countNumber < keysMap.size(); countNumber++) {
            int count = keysMap.get(countNumber);

            for (int i = sum; i < sum + count; i++) {
                List<String> columnNames = new ArrayList<>();
                List<String> values = new ArrayList<>();
                columnNames.add(tableGenerator.getPrimaryKeyGenerator().getColumnName());
                columnNames.add(parentForeignKeyName);
                values.add(generatedNonNullKeys.get(i));
                values.add(childPrimaryKeyValues.get(countNumber));
                for (ColumnGenerator columnGenerator : tableGenerator.getColumnGenerators()) {
                    columnNames.addAll(Arrays.stream(columnGenerator.getColumnNames()).toList());
                    values.addAll(Arrays.stream(columnGenerator.getNextValues()).toList());
                }
                queryExecutor.executeQuery(createInsertQuery(tableGenerator.getTableName(), columnNames, values));
            }
            sum += count;
        }

        for (String nullKey : generatedNullKeys) {
            List<String> columnNames = new ArrayList<>();
            List<String> values = new ArrayList<>();
            columnNames.add(tableGenerator.getPrimaryKeyGenerator().getColumnName());
            values.add(nullKey);
            for (ColumnGenerator columnGenerator : tableGenerator.getColumnGenerators()) {
                columnNames.addAll(Arrays.stream(columnGenerator.getColumnNames()).toList());
                values.addAll(Arrays.stream(columnGenerator.getNextValues()).toList());
            }
            queryExecutor.executeQuery(createInsertQuery(tableGenerator.getTableName(), columnNames, values));
        }

        queryExecutor.commit();
        System.out.println("FILLED TABLE " + tableGenerator.getTableName() + " WITH " + allGeneratedKeys.size() + " ROWS");


        for (RelationMapElement element : graphNode.getChildren()) {
            TableRelationGraphNode node = element.getNode();
            if (node.getTableName().equals(childTableName)) continue;
            executor.execute(() -> {
                try {
                    fillChildTable(element.getNode(),
                            executor,
                            queryTool,
                            tableGenerators.stream().filter(tg -> tg.getTableName().equals(node.getTableName())).findFirst().get(),
                            tableGenerator.getTableName(),
                            reduceRandomly(allGeneratedKeys, element.getTargetZeroChance()),
                            tableGenerator.getPrimaryKeyGenerator().getColumnName(),
                            element.getForeignKeyName(),
                            element.getDistribution(),
                            element.getSourceZeroChance()
                    );
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

        }

        GlobalTimeMeasurer.tick();
    }

    private String getTableCreationQuery(Table table) {
        StringBuilder query = new StringBuilder("CREATE TABLE ");
        query.append(table.getName());
        query.append(" (");
        for (int i = 0; i < table.getColumns().size() - 1; i++) {
            Column column = table.getColumns().get(i);
            query.append(getColumnCreationQuery(column));
            query.append(", ");
        }
        query.append(getColumnCreationQuery(table.getColumns().getLast()));
        query.append(")");
        return query.toString();
    }

    private String getColumnCreationQuery(Column column) {
        StringBuilder query = new StringBuilder();
        if (column.getType() == ValueType.OBJECT_ID) {
            query.append(column.getName());
            query.append(' ');
            query.append("VARCHAR");
        } else if (column.getType() == ValueType.FLOAT_INTERVAL) {
            query.append(getIntervalCreationQuery(column.getName(), ValueType.FLOAT));
        } else if (column.getType() == ValueType.DATE_INTERVAL) {
            query.append(getIntervalCreationQuery(column.getName(), ValueType.DATE));
        } else {
            query.append(column.getName());
            query.append(' ');
            query.append(column.getType());
        }
        return query.toString();
    }

    private String getIntervalCreationQuery(String columnName, ValueType type) {
        return columnName +
                "_start " +
                type +
                ", " +
                columnName +
                "_end " +
                type;
    }

    private String createInsertQuery(String tableName, List<String> columnNames, List<String> values) {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(tableName);
        query.append(" (");
        query.append(String.join(",", columnNames));
        query.append(") VALUES (");
        query.append(String.join(", ", values));
        query.append(");");
        return query.toString();
    }

    private String createUpdateQuery(String tableName, String primaryKeyName, String primaryKeyValue, String updateColumnName, String newValue) {
        String query = "UPDATE " + tableName +
                " SET " +
                updateColumnName +
                " = " +
                newValue +
                " WHERE " +
                primaryKeyName +
                " = " +
                primaryKeyValue;
        return query;
    }

    private String createPrimaryKeyConstraintQuery(String tableName, String columnName) {
        return String.format("""
                    ALTER TABLE %s ALTER COLUMN %s SET NOT NULL;
                    ALTER TABLE %s ADD PRIMARY KEY (%s);
                """, tableName, columnName, tableName, columnName);
    }

    private String createForeignKeyConstraintQuery(String sourceTable, String sourceColumn, String targetTable, String targetColumn) {
        return String.format("""
                ALTER TABLE %s
                ADD CONSTRAINT %s
                FOREIGN KEY (%s)\s
                REFERENCES %s(%s);
                """, sourceTable, "fK" + new Random().nextInt(1000000), sourceColumn, targetTable, targetColumn);
    }

    private String createUniqueConstraintQuery(String tableName, String columnName) {
        return String.format("ALTER TABLE %s ADD CONSTRAINT %s UNIQUE (%s);", tableName, "unique" + new Random().nextInt(1000000), columnName);
    }

    private String createNotNullConstraintQuery(String tableName, String columnName) {
        return String.format("ALTER TABLE %s ALTER COLUMN %s SET NOT NULL;", tableName, columnName);
    }

    private List<String> reduceRandomly(List<String> original, float chanceOfReduction) {
        List<String> res = new ArrayList<>(original);
        Collections.shuffle(res);
        for (int i = 0; i < (int) (original.size() * chanceOfReduction); i++) {
            res.removeLast();
        }
        return res;
    }

}
