import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.text.DecimalFormat;

public final class HausverwaltungClient {
    private enum Commands {
        LIST("list"),
        ADD("add"),
        DELETE("delete"),
        COUNT("count"),
        MEANCOSTS("meancosts"),
        OLDEST("oldest");

        private final String key;

        Commands(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public static Commands forKey(String key) {
            for (Commands v : values()) {
                if (v.key.equals(key)) {
                    return v;
                }
            }
            throw PARAM_INVALID;
        }
    }

    private enum WohnungsType {
        EIGENTUMS_WOHNUNG("EW", EigentumsWohnung.class),
        MIET_WOHNUNG("MW", MietWohnung.class);
        private final String str;
        private final Class<?> cls;

        WohnungsType(String str, Class<?> cls) {
            this.str = str;
            this.cls = cls;
        }

        public String getStr() {
            return str;
        }

        public Class<?> getCls() {
            return cls;
        }

        public static WohnungsType forStr(String str) {
            for (WohnungsType v : values()) {
                if (v.str.equals(str)) {
                    return v;
                }
            }
            throw PARAM_INVALID;
        }

        public static WohnungsType forCls(Class<?> cls) {
            for (WohnungsType v : values()) {
                if (v.cls.isAssignableFrom(cls)) {
                    return v;
                }
            }
            throw PARAM_INVALID;
        }
    }

    public static class Adresse implements Serializable {
        private static final long serialVersionUID = 3380825380726751815L;
        private final int plz;
        private final String strasse;
        private final int hausnummer;
        private final int top;

        public Adresse(int plz, String strasse, int hausnummer, int top) {
            this.plz = plz;
            this.strasse = strasse;
            this.hausnummer = hausnummer;
            this.top = top;
        }

        public int getPlz() {
            return plz;
        }

        public String getStrasse() {
            return strasse;
        }

        public int getHausnummer() {
            return hausnummer;
        }

        public int getTop() {
            return top;
        }

        @Override
        public String toString() {
            return "(PLZ) " + plz + ", " + strasse + " " + hausnummer + " / " + top;
        }
    }

    public static final IllegalArgumentException PARAM_INVALID = new IllegalArgumentException("Error: Parameter ungueltig.");
    public static final IllegalArgumentException BAUJAHR_INVALID = new IllegalArgumentException("Error: Baujahr ungueltig.");
    public static final DecimalFormat DF = new DecimalFormat("0.00");

    public static IllegalArgumentException wohnungAlreadyExists(int id) {
        return new IllegalArgumentException("Error: Wohnung bereits vorhanden. (id=" + id + ')');
    }

    public static IllegalArgumentException wohnungDoesntExist(int id) {
        return new IllegalArgumentException("Error: Wohnung nicht vorhanden. (id=" + id + ')');
    }

    public static void main(String[] args) {
        try {
            Hausverwaltung hausverwaltung = new Hausverwaltung(new HausverwaltungSerializationDAO(Paths.get(args[0])));
            Commands rootCommand = Commands.forKey(args[1]);

            switch (rootCommand) {
                case LIST:
                    list(hausverwaltung, args);
                    break;
                case ADD:
                    add(hausverwaltung, args);
                    break;
                case DELETE:
                    delete(hausverwaltung, args);
                    break;
                case COUNT:
                    count(hausverwaltung, args);
                    break;
                case MEANCOSTS:
                    meancosts(hausverwaltung);
                    break;
                case OLDEST:
                    oldest(hausverwaltung);
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException exc) {
            System.out.println(PARAM_INVALID.getMessage());
        } catch (IllegalArgumentException exc) {
            System.out.println(exc.getMessage());
        }
    }

    public static void list(Hausverwaltung hausverwaltung, String[] args) {
        if (args.length <= 2) {
            for (Wohnung w : hausverwaltung.getAllData()) {
                System.out.println(w);
            }
        } else {
            Wohnung w = hausverwaltung.getDataOf(Integer.parseInt(args[2]));
            if (w != null) {
                System.out.println(w);
            }
        }
    }

    public static void add(Hausverwaltung hausverwaltung, String[] args) {
        WohnungsType type = WohnungsType.forStr(args[2]);
        int id = Integer.parseInt(args[3]);
        double flaeche = Double.parseDouble(args[4]);
        int zimmer = Integer.parseInt(args[5]);
        int stock = Integer.parseInt(args[6]);
        int baujahr = Integer.parseInt(args[7]);
        int plz = Integer.parseInt(args[8]);
        String strasse = args[9];
        int hausnummer = Integer.parseInt(args[10]);
        int top = Integer.parseInt(args[11]);

        switch (type) {
            case EIGENTUMS_WOHNUNG:
                BigDecimal betriebskosten = new BigDecimal(args[12]);
                BigDecimal ruecklage = new BigDecimal(args[13]);
                hausverwaltung.addWohnung(new EigentumsWohnung(
                    id,
                    flaeche,
                    zimmer,
                    stock,
                    baujahr,
                    new Adresse(plz, strasse, hausnummer, top),
                    betriebskosten,
                    ruecklage
                ));
                break;
            case MIET_WOHNUNG:
                BigDecimal mietkosten = new BigDecimal(args[12]);
                int mieter = Integer.parseInt(args[13]);
                hausverwaltung.addWohnung(new MietWohnung(
                    id,
                    flaeche,
                    zimmer,
                    stock,
                    baujahr,
                    new Adresse(plz, strasse, hausnummer, top),
                    mietkosten,
                    mieter
                ));
                break;
        }
        System.out.println("Info: Wohnung " + id + " added.");
    }

    private static void delete(Hausverwaltung hausverwaltung, String[] args) {
        int id = Integer.parseInt(args[2]);
        hausverwaltung.deleteWohnung(id);
        System.out.println("Info: Wohnung " + id + " deleted.");
    }

    private static void count(Hausverwaltung hausverwaltung, String[] args) {
        if (args.length <= 2) {
            System.out.println(hausverwaltung.count(Wohnung.class));
        } else {
            System.out.println(hausverwaltung.count(WohnungsType.forStr(args[2]).getCls()));
        }
    }

    private static void meancosts(Hausverwaltung hausverwaltung) {
        System.out.println(DF.format(hausverwaltung.averageMonthlyCosts().setScale(2, RoundingMode.HALF_UP)));
    }

    private static void oldest(Hausverwaltung hausverwaltung) {
        for (int id : hausverwaltung.oldestWohnungId()) {
            System.out.println("Id: " + id);
        }
    }
}