import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class MietWohnung extends Wohnung {
    private static final long serialVersionUID = -3980588736879024607L;
    private final BigDecimal mietkosten;
    private final int anzahlMieter;

    public MietWohnung(
        int id,
        double flaeche,
        int zimmeranzahl,
        int stockwerk,
        LocalDate baujahr,
        HausverwaltungClient.Adresse adresse,
        BigDecimal mietkosten,
        int anzahlMieter
    ) {
        super(id, flaeche, zimmeranzahl, stockwerk, baujahr, adresse);
        if (mietkosten.compareTo(BigDecimal.ZERO) <= 0) throw HausverwaltungClient.PARAM_INVALID;
        if (anzahlMieter <= 0) throw HausverwaltungClient.PARAM_INVALID;
        this.mietkosten = mietkosten.setScale(2, RoundingMode.HALF_UP);
        this.anzahlMieter = anzahlMieter;
    }

    public MietWohnung(
        int id,
        double flaeche,
        int zimmeranzahl,
        int stockwerk,
        int baujahr,
        HausverwaltungClient.Adresse adresse,
        BigDecimal mietkosten,
        int anzahlMieter
    ) {
        this(
            id,
            flaeche,
            zimmeranzahl,
            stockwerk,
            LocalDate.of(baujahr, 1, 1),
            adresse,
            mietkosten,
            anzahlMieter
        );
    }

    @Override
    public BigDecimal gesamtKosten() {
        return mietkosten.multiply(BigDecimal.valueOf(getFlaeche()))
            .multiply(BigDecimal.valueOf(1.0 + Math.min((anzahlMieter - 1) * 0.025, 0.1)));
    }

    @Override
    public String getType() {
        return "MW";
    }

    @Override
    public String toString() {
        return super.toString()
            + kv("Miete/m2", HausverwaltungClient.DF.format(mietkosten))
            + kv("Anzahl Mieter", String.valueOf(anzahlMieter));
    }
}