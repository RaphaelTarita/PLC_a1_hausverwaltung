import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class HausverwaltungSerializationDAO implements HausverwaltungDAO {
    private final Path savefile;

    @SuppressWarnings("unchecked")
    private static <T> T readObjectUnchecked(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        return (T) ois.readObject();
    }

    public HausverwaltungSerializationDAO(Path savefile) {
        this.savefile = savefile;
    }

    private void saveWohnungen(List<Wohnung> wohnungen) {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(
            savefile,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.CREATE
        ))) {
            oos.writeObject(wohnungen);
        } catch (IOException exc) {
            System.err.println("Fehler bei Serialisierung: " + exc);
            System.exit(1);
            throw new AssertionError("unreachable");
        }
    }

    @Override
    public List<Wohnung> getWohnungen() {
        if (!Files.exists(savefile)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(savefile))) {
            return readObjectUnchecked(ois);
        } catch (IOException | ClassNotFoundException exc) {
            System.err.println("Fehler bei Deserialisierung: " + exc);
            System.exit(1);
            throw new AssertionError("unreachable");
        }
    }

    @Override
    public Wohnung getWohnungbyId(int id) {
        List<Wohnung> deserialized = getWohnungen();
        return deserialized.stream()
            .filter(wohnung -> wohnung.getId() == id)
            .findAny()
            .orElse(null);
    }

    @Override
    public void saveWohnung(Wohnung wohnung) {
        List<Wohnung> deserialized = getWohnungen();
        if (deserialized.stream().anyMatch(w -> wohnung.getId() == w.getId())) {
            throw HausverwaltungClient.wohnungAlreadyExists(wohnung.getId());
        }
        deserialized.add(wohnung);
        saveWohnungen(deserialized);
    }

    @Override
    public void deleteWohnung(int id) {
        List<Wohnung> deserialized = getWohnungen();
        if (!deserialized.removeIf(wohnung -> id == wohnung.getId())) {
            throw HausverwaltungClient.wohnungDoesntExist(id);
        }
        saveWohnungen(deserialized);
    }
}