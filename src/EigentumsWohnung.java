import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class EigentumsWohnung extends Wohnung {
    private static final long serialVersionUID = -4761102347229678219L;
    private final BigDecimal betriebskosten;
    private final BigDecimal repararturRuecklage;

    public EigentumsWohnung(
        int id,
        double flaeche,
        int zimmeranzahl,
        int stockwerk,
        LocalDate baujahr,
        HausverwaltungClient.Adresse adresse,
        BigDecimal betriebskosten,
        BigDecimal repararturRuecklage
    ) {
        super(id, flaeche, zimmeranzahl, stockwerk, baujahr, adresse);
        if (betriebskosten.compareTo(BigDecimal.ZERO) <= 0) throw HausverwaltungClient.PARAM_INVALID;
        if (repararturRuecklage.compareTo(BigDecimal.ZERO) <= 0) throw HausverwaltungClient.PARAM_INVALID;
        this.betriebskosten = betriebskosten.setScale(2, RoundingMode.HALF_UP);
        this.repararturRuecklage = repararturRuecklage.setScale(2, RoundingMode.HALF_UP);
    }

    public EigentumsWohnung(
        int id,
        double flaeche,
        int zimmeranzahl,
        int stockwerk,
        int baujahr,
        HausverwaltungClient.Adresse adresse,
        BigDecimal betriebskosten,
        BigDecimal repararturRuecklage
    ) {
        this(
            id,
            flaeche,
            zimmeranzahl,
            stockwerk,
            LocalDate.of(baujahr, 1, 1),
            adresse,
            betriebskosten,
            repararturRuecklage
        );
    }

    public BigDecimal getBetriebskosten() {
        return betriebskosten;
    }

    public BigDecimal getRepararturRuecklage() {
        return repararturRuecklage;
    }

    @Override
    public BigDecimal gesamtKosten() {
        return (betriebskosten.add(repararturRuecklage))
            .multiply(BigDecimal.valueOf(getFlaeche()))
            .multiply(BigDecimal.valueOf(1.0 + 0.02 * getStockwerk()));
    }

    @Override
    public String getType() {
        return "EW";
    }

    @Override
    public String toString() {
        return super.toString()
            + kv("Betriebskosten", HausverwaltungClient.DF.format(betriebskosten))
            + kv("Ruecklage", HausverwaltungClient.DF.format(repararturRuecklage));
    }
}