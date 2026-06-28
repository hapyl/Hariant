package me.hapyl.hariant.database.serialize;

import me.hapyl.hariant.HariantPlugin;
import me.hapyl.hariant.database.PlayerDatabase;
import me.hapyl.hariant.database.PlayerDatabaseEntry;
import me.hapyl.hariant.database.problem.ProblemReporter;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public interface MongoSerializable {
    
    void write(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter);
    
    void read(@NotNull PlayerDatabase database, @NotNull Document document, @NotNull ProblemReporter problemReporter);
    
    @NotNull
    default Document writeToNewDocument(@NotNull PlayerDatabase database, @NotNull ProblemReporter problemReporter) {
        final Document document = new Document();
        write(database, document, problemReporter);
        
        return document;
    }
    
    class Deserializer {
        private static final Class<?>[] CONSTRUCTOR_PARAMETERS = { PlayerDatabase.class, Document.class, String.class };
        private static final String CONSTRUCTOR_PARAMETERS_NAMES = Arrays.stream(CONSTRUCTOR_PARAMETERS).map(Class::getSimpleName).collect(Collectors.joining(", "));
        
        private Deserializer() {
        }
        
        // #norender
        @NotNull
        public static <E extends PlayerDatabaseEntry> E deserialize(@NotNull String parent, @NotNull Class<E> clazz, @NotNull PlayerDatabase playerDatabase, @NotNull Document root, @NotNull ProblemReporter problemReporter) {
            final String simpleClassName = clazz.getSimpleName();
            
            try {
                // Find the serialization constructor
                final Constructor<E> constructor = clazz.getDeclaredConstructor(CONSTRUCTOR_PARAMETERS);
                constructor.setAccessible(true);
                
                final int modifiers = constructor.getModifiers();
                
                if (!constructor.isAnnotationPresent(MongoSerializableConstructor.class)) {
                    throw new RuntimeException("Serialization constructor must be annotated with `%s`!".formatted(MongoSerializableConstructor.class.getSimpleName()));
                }
                
                if (!Modifier.isPrivate(modifiers)) {
                    throw new RuntimeException("Serialization constructor must be private!");
                }
                
                final Document document = root.get(parent, new Document());
                
                final E newInstance = constructor.newInstance(playerDatabase, document, parent);
                newInstance.read(playerDatabase, root.get(newInstance.getParent(), document), problemReporter);
                
                return newInstance;
            }
            catch (NoSuchMethodException noSuchMethodException) {
                throw HariantPlugin.severeExceptionShutdownServer(new RuntimeException(
                        "Error deserializing `%s`: Missing constructor `%s(%s)`!".formatted(simpleClassName, clazz.getSimpleName(), CONSTRUCTOR_PARAMETERS_NAMES)
                ));
            }
            catch (Exception ex) {
                throw HariantPlugin.severeExceptionShutdownServer(new RuntimeException("Error deserializing `%s`: %s".formatted(simpleClassName, ex.getMessage()), ex));
            }
        }
    }
    
}
