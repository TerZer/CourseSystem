package lt.terzer.sql.data;

import java.util.ArrayList;

public class SerializableList extends ArrayList<Integer> {

    public String serialize(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0;i < size();i++){
            if(i+1 != size())
                stringBuilder.append(get(i) + ":");
            else
                stringBuilder.append(get(i));
        }
        return stringBuilder.toString();
    }

    public static SerializableList deserialize(String str){
        SerializableList serializableList = new SerializableList();
        if(str == null)
            return serializableList;
        if(str.trim().equals(""))
            return serializableList;

        String[] split = str.split(":");
        for(String number : split)
            serializableList.add(Integer.valueOf(number));
        return serializableList;
    }


}
