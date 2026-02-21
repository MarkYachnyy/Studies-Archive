package ru.vsu.cs.yachnyy_m_a;

import ru.vsu.cs.yachnyy_m_a.util.ArrayUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Maze {

    private static class MazeCell {

        private final int[] coordinates;

        public MazeCell(int[] coordinates) {
            this.coordinates = coordinates;
        }

        public int[] getCoordinates() {
            return coordinates;
        }

        @Override
        public boolean equals(Object obj) {
            return Arrays.equals(this.coordinates, ((MazeCell) obj).coordinates);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(coordinates);
        }

        @Override
        public String toString() {
            return "(" + Arrays.stream(coordinates).boxed().map(Object::toString).collect(Collectors.joining(",")) + ")";
        }
    }

    private class Wall {
        private MazeCell first;
        private MazeCell second;

        public Wall(MazeCell first, MazeCell second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public int hashCode() {
            return first.hashCode() + second.hashCode();
        }

        @Override
        public boolean equals(Object o){
            Wall w2 = (Wall) o;
            return this.first.equals(w2.first) && this.second.equals(w2.second) ||  this.first.equals(w2.second) && this.second.equals(w2.first);
        }

        @Override
        public String toString() {
            return first.toString() + "-" + second.toString();
        }

        public boolean isBorder(){
            for (int i = 0; i < dimension_count; i++) {
                if(first.getCoordinates()[i] == -1 || first.getCoordinates()[i] == dimensions[i] || second.getCoordinates()[i] == -1 || second.getCoordinates()[i] == dimensions[i]){
                    return true;
                }
            }
            return false;
        }
    }

    public static class Path implements Iterable<int[]>{

        private List<MazeCell> cells;

        private Path(List<MazeCell> cells) {
            this.cells = cells;
        }

        public boolean contains(int[] cell){
            return cells.contains(new MazeCell(cell));
        }

        public int indexOf(int[] cell){
            return cells.indexOf(new MazeCell(cell));
        }

        public int size(){
            return cells.size();
        }

        @Override
        public Iterator<int[]> iterator() {
            return new Iterator<int[]>() {

                private Iterator<MazeCell> itr = cells.iterator();

                @Override
                public boolean hasNext() {
                    return itr.hasNext();
                }

                @Override
                public int[] next() {
                    return itr.next().getCoordinates();
                }
            };
        }

        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            for(MazeCell cell: this.cells){
                res.append(cell.toString()).append('\n');
            }
            return res.toString();
        }
    }

    private final int dimension_count;
    private final int[] dimensions;
    private HashSet<Wall> walls;

    public Maze(int dimension_count, int... dimensions) {
        if(dimension_count != dimensions.length) throw new IllegalArgumentException("dimensions count mismatch");
        this.dimension_count = dimension_count;
        this.dimensions = dimensions;
        walls = new HashSet<>();
        addBorders();
    }

    public int getDimensionCount() {
        return dimension_count;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    public boolean addWall(int[] c1, int[] c2){
        if(c1.length != dimension_count || c2.length != dimension_count) throw new IllegalArgumentException();
        return walls.add(new Wall(new MazeCell(c1), new MazeCell(c2)));
    }

    public boolean removeWall(int[] c1, int[] c2){
        Wall wall = new Wall(new MazeCell(c1), new MazeCell(c2));
        if(wall.isBorder()) throw new IllegalArgumentException("Trying to remove border of maze");
        return walls.remove(wall);
    }

    public boolean containsWall(int[] c1, int[] c2){
        return walls.contains(new Wall(new MazeCell(c1), new MazeCell(c2)));
    }

    public Path findPath(int[] enter, int[] exit) {
        if(enter.length != dimension_count || exit.length != dimension_count) throw new IllegalArgumentException("dimensions count mismatch");
        HashSet<MazeCell> visits = new HashSet<>();
        LinkedList<MazeCell> res = new LinkedList<>();
        //if(findPath(new MazeCell(enter), new MazeCell(exit), visits, res)) res.add(enter);
        findPath(new MazeCell(enter), new MazeCell(exit), visits, res);
        return new Path(res);
    }

    private boolean findPath(MazeCell cell, MazeCell exit, HashSet<MazeCell> visits, List<MazeCell> output) {
        visits.add(cell);
        if (cell.equals(exit)) {
            output.add(0, cell);
            return true;
        }
        for (MazeCell neighbour : neighbours(cell)) {
            if (!visits.contains(neighbour) && !walls.contains(new Wall(cell, neighbour)) && findPath(neighbour, exit, visits, output)) {
                output.add(0, cell);
                return true;
            }
        }
        return false;
    }

    public Iterable<MazeCell> neighbours(MazeCell cell) {
        LinkedList<MazeCell> res = new LinkedList<>();
        int[] tmp = Arrays.copyOf(cell.coordinates, this.dimension_count);
        for (int i = 0; i < this.dimension_count; i++) {
            tmp[i] -= 1;
            res.add(new MazeCell(Arrays.copyOf(tmp, this.dimension_count)));
            tmp[i] += 2;
            res.add(new MazeCell(Arrays.copyOf(tmp, this.dimension_count)));
            tmp[i] -= 1;
        }
        return res;
    }

    private static void neighbours(int[] coords, int pos, List<MazeCell> output) {
        for (int i = -1; i <= 1; i++) {
            coords[pos] += i;
            if (pos < coords.length - 1) {
                neighbours(coords, pos + 1, output);
            } else {
                int[] new_c = Arrays.copyOf(coords, coords.length);
                output.add(new MazeCell(new_c));
            }
            coords[pos] -= i;
        }
    }

    private void addBorders(){
        for(int dimension_at = 0; dimension_at < this.dimension_count; dimension_at++){
            addBordersAt(new int[this.dimension_count], dimension_at, 0);
        }
    }

    private void addBordersAt(int[] tmp, int dimension_at, int changing_dimension){
        if(changing_dimension == this.dimension_count){
            int[] c1 = Arrays.copyOf(tmp, this.dimension_count);
            int[] c2 = Arrays.copyOf(tmp, this.dimension_count);
            c2[dimension_at] = c1[dimension_at] == 0 ? -1 : this.dimensions[dimension_at];
            this.addWall(c1, c2);
            return;
        }
        if(changing_dimension == dimension_at){
            for(int i: new int[]{0, this.dimensions[dimension_at] - 1}){
                tmp[changing_dimension] = i;
                addBordersAt(tmp, dimension_at, changing_dimension + 1);
            }
        } else {
            for(int i = 0; i < this.dimensions[changing_dimension]; i++){
                tmp[changing_dimension] = i;
                addBordersAt(tmp, dimension_at, changing_dimension + 1);
            }
        }
    }

    public static Maze fromString(String src) throws IOException {
        int dimension_count;
        Matcher int_matcher = Pattern.compile("\\d+").matcher(src);
        if(int_matcher.find()){
            dimension_count = Integer.parseInt(src.substring(int_matcher.start(), int_matcher.end()).trim());
        } else {
            throw new IOException();
        }

        int[] dimensions = new int[dimension_count];

        for(int i = 0; i < dimension_count; i++){
            int_matcher.find();
            dimensions[i] = Integer.parseInt(src.substring(int_matcher.start(), int_matcher.end()).trim());
        }

        Maze maze = new Maze(dimension_count, dimensions);

        Matcher wall_matcher = Pattern.compile("\\(([^()]+)\\)-\\(([^()]+)\\)").matcher(src);
        while (wall_matcher.find()){
            int[] cell1 = ArrayUtils.toIntArray(wall_matcher.group(1));
            int[] cell2 = ArrayUtils.toIntArray(wall_matcher.group(2));
            maze.addWall(cell1, cell2);
        }

        return maze;
    }

    public String toString(){
        StringBuilder res = new StringBuilder();
        res.append(dimension_count).append(' ');
        for(int i: dimensions){
            res.append(i).append(' ');
        }
        res.append('\n');
        for(Wall wall: walls){
            if(!wall.isBorder()) res.append(wall.toString()).append('\n');
        }
        return res.toString();
    }
}
