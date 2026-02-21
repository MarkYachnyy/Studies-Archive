package ru.vsu.cs.iachnyi_m_a.database_anonymizer;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.datasource.HikariDataSourceFactory;

import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.DatabaseGenerator;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.query.QueryTool;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.constraint.ConstraintSet;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.RuleSet;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.schema.DatabaseSchema;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        HikariDataSource source = HikariDataSourceFactory.create("anonymizer", "postgres", "1234", 1000, 10);
        DatabaseGenerator generator = new DatabaseGenerator(new QueryTool(source));
        DatabaseSchema schema = new Gson().fromJson(readFileToString("./test_data/schema.json"), DatabaseSchema.class);
        ConstraintSet constraintSet = new Gson().fromJson(readFileToString("./test_data/constraint.json"), ConstraintSet.class);
        RuleSet ruleSet = new Gson().fromJson(readFileToString("./test_data/rules.json"), RuleSet.class);
        generator.fillDatabase(schema, constraintSet, ruleSet, "users", 1000);
    }

    public static String readFileToString(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

}
