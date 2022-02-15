import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public abstract class Wohnung implements Serializable {
    private static final long serialVersionUID = -3019627042156823628L;

    protected static String kv(String key, String value) {
        StringBuilder sb = new StringBuilder(key);
        sb.append(':');
        for (int i = key.length(); i < 15; i++) {
            sb.append(" ");
        }
        return sb.append(value)
            .append('\n')
            .toString();
    }

    private final int id;
    private final double flaeche;
    private final int zimmeranzahl;
    private final int stockwerk;
    private final LocalDate baujahr;
    private final HausverwaltungClient.Adresse adresse;

    public Wohnung(int id, double flaeche, int zimmeranzahl, int stockwerk, LocalDate baujahr, HausverwaltungClient.Adresse adresse) {
        if (baujahr.isAfter(LocalDate.now())) throw HausverwaltungClient.BAUJAHR_INVALID;
        if (flaeche <= 0) throw HausverwaltungClient.PARAM_INVALID;
        if (zimmeranzahl <= 0) throw HausverwaltungClient.PARAM_INVALID;
        this.id = id;
        this.flaeche = flaeche;
        this.zimmeranzahl = zimmeranzahl;
        this.stockwerk = stockwerk;
        this.baujahr = baujahr;
        this.adresse = adresse;
    }

    public Wohnung(int id, double flaeche, int zimmeranzahl, int stockwerk, int baujahr, HausverwaltungClient.Adresse adresse) {
        this(
            id,
            flaeche,
            zimmeranzahl,
            stockwerk,
            LocalDate.of(baujahr, 1, 1),
            adresse
        );
    }

    public int getId() {
        return id;
    }

    public double getFlaeche() {
        return flaeche;
    }

    public int getZimmeranzahl() {
        return zimmeranzahl;
    }

    public int getStockwerk() {
        return stockwerk;
    }

    public LocalDate getBaujahr() {
        return baujahr;
    }

    public HausverwaltungClient.Adresse getAdresse() {
        return adresse;
    }

    public Period alter() {
        return baujahr.until(LocalDate.now());
    }

    public abstract BigDecimal gesamtKosten();

    public abstract String getType();

    @Override
    public String toString() {
        return kv("Typ", getType())
            + kv("Id", String.valueOf(id))
            + kv("Flaeche", HausverwaltungClient.DF.format(flaeche))
            + kv("Zimmer", String.valueOf(zimmeranzahl))
            + kv("Stock", String.valueOf(stockwerk))
            + kv("Baujahr", String.valueOf(baujahr.getYear()))
            + kv("PLZ", String.valueOf(adresse.getPlz()))
            + kv("Strasse", adresse.getStrasse())
            + kv("Hausnummer", String.valueOf(adresse.getHausnummer()))
            + kv("Top", String.valueOf(adresse.getTop()));
    }
}
