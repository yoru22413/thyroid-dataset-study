package dm.backend.table;

import dm.backend.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class Table {
    private ArrayList<Column> columns;
    public Table(ArrayList<Column> columns){
        this.columns = columns;
    }

    public Table() {
        this.columns = new ArrayList<>();
    }

    public static Table fromCsv(String path, ColumnType[] types, String[] columnNames){
        Table t = new Table();
        URL url = Utils.class.getClassLoader().getResource(path);
        BufferedReader br;
        ArrayList<String> text[] = new ArrayList[types.length];
        for(int i=0;i<text.length;i++){
            text[i] = new ArrayList<>();
        }
        try {
            assert url != null;
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = br.readLine();
            while(line != null){
                String[] tokens = line.split(",");
                for(int i=0;i<tokens.length;i++){
                    text[i].add(tokens[i]);
                }
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Column[] columns = new Column[types.length];
        for(int i=0;i<columns.length;i++){
            if (types[i] == ColumnType.DOUBLE){
                columns[i] = new DoubleColumn(text[i].toArray(new String[0]), columnNames[i]);
            }
            else if(types[i] == ColumnType.INTEGER){
                columns[i] = new IntegerColumn(text[i].toArray(new String[0]), columnNames[i]);
            }
        }
        t.columns.addAll(Arrays.asList(columns));
        return t;
    }

    @Override
    public String toString() {
        int length = columns.get(0).size();
        StringBuilder s = new StringBuilder();
        for(int j=0;j<length;j++){
            for(int i=0;i<columns.size();i++){
                s.append(columns.get(i).get(j)).append(", ");
            }
            s.replace(s.length()-2, s.length(), "").append("\n");
        }
        return s.toString();
    }

    public IntegerColumn intColumn(int i){
        return (IntegerColumn)columns.get(i);
    }

    public DoubleColumn doubleColumn(int i){
        return (DoubleColumn) columns.get(i);
    }

    public int height(){
        return columns.get(0).size();
    }

    public int width(){
        return columns.size();
    }

    public Column column(int i){
        return columns.get(i);
    }

    public void setColumn(int i, Column col){
        columns.set(i, col);
    }

    public Row getRow(int i){
        Object[] t = new Object[columns.size()];
        for (int j = 0; j < t.length; j++) {
            t[j] = columns.get(j).get(i);
        }
        return new Row(t);
    }

    public void removeColumn(int i){
        columns.remove(i);
    }
    public void addColumn(int i, Column column){
        columns.add(i, column);
    }
}
