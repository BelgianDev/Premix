package userend;

import com.google.gson.JsonObject;
import fr.atlasworld.common.file.reader.JsonFileReader;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("temp.txt");

        JsonFileReader<JsonObject> reader = new JsonFileReader<>(file, JsonObject.class);

        try {
            reader.readElseCreate(new JsonObject());
            reader.write(new JsonObject());
        } catch (IOException ex) {
            System.out.println("Little error: " + ex.getMessage());
        }
    }
}
