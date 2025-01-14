package be.raft.premix.mixin;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.WeakHashMap;

public class MixinConfigFile {
    private static final Gson GSON = new Gson();
    private static final WeakHashMap<String, Boolean> PROTECTION_COMPUTING_CACHE = new WeakHashMap<>();
    private static final Set<String> HARDCODED_PROTECTION_RULES = Set.of(
            // Critical Core Java code.
            "java.*",
            "javax.*",
            "sun.*",
            "com.sun.*",
            "jdk.*",

            // Protect own library.
            "be.raft.premix.*"
    );

    public static final String FILE = "mixin.json";


    private final @SerializedName("mixin") Set<String> mixinClasses;
    private final @Nullable @SerializedName("protected") Set<String> protectedClasses;

    {
        this.validate(); // Validate the data before anything else.
    }

    public static MixinConfigFile loadFromCandidate(ClassLoader loader) throws IOException {
        try (InputStream stream = loader.getResourceAsStream(FILE);
            Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {

            return GSON.fromJson(reader, MixinConfigFile.class);
        }
    }

    private MixinConfigFile(Set<String> mixinClasses, @Nullable Set<String> protectedClasses) {
        this.mixinClasses = mixinClasses;
        this.protectedClasses = protectedClasses;
    }

    private void validate() {
        Preconditions.checkArgument(this.mixinClasses != null, "Mixin classes must be defined in '" + FILE + "'!");

        if (this.protectedClasses == null)
            return;

        this.protectedClasses.forEach(className -> {
            if (className.isEmpty() || (className.contains("*") && !className.endsWith(".*")))
                throw new IllegalArgumentException("Invalid mixin protected class: '" + className + "'. " +
                        "Class names cannot be empty, and wildcards must be at the end (e.g., 'com.example.*').");
        });
    }

    @NotNull
    public Set<String> mixinClasses() {
        return this.mixinClasses;
    }

    public boolean isProtected(String className) {
        if (PROTECTION_COMPUTING_CACHE.containsKey(className))
            return PROTECTION_COMPUTING_CACHE.get(className);

        if (this.protectedClasses != null && this.checkProtectionRules(className, this.protectedClasses)) {
            PROTECTION_COMPUTING_CACHE.put(className, true);
            System.out.println("Class" + className + " is protected by user rules.");
            return true;
        }

        boolean result = this.checkProtectionRules(className, HARDCODED_PROTECTION_RULES);
        PROTECTION_COMPUTING_CACHE.put(className, result);

        return result;
    }

    private boolean checkProtectionRules(String className, Set<String> rules) {
        for (String rule : rules) {
            if (rule.endsWith(".*")) {
                String pkg = rule.substring(0, rule.length() - 2);
                if (className.startsWith(pkg))
                    return true;

                continue;
            }

            if (rule.equals(className))
                return true;
        }

        return false;
    }
}
