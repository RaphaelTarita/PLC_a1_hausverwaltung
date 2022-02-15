import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class Hausverwaltung {
    private final HausverwaltungDAO hausverwaltungDAO;

    public Hausverwaltung(HausverwaltungDAO hausverwaltungDAO) {
        this.hausverwaltungDAO = hausverwaltungDAO;
    }

    public List<Wohnung> getAllData() {
        return hausverwaltungDAO.getWohnungen();
    }

    public Wohnung getDataOf(int id) {
        return hausverwaltungDAO.getWohnungbyId(id);
    }

    public void addWohnung(Wohnung w) {
        hausverwaltungDAO.saveWohnung(w);
    }

    public void deleteWohnung(int id) {
        hausverwaltungDAO.deleteWohnung(id);
    }

    public long count(Class<?> clazz) {
        return hausverwaltungDAO.getWohnungen()
            .stream()
            .filter(clazz::isInstance)
            .count();
    }

    public BigDecimal averageMonthlyCosts() {
        List<Wohnung> wohnungen = hausverwaltungDAO.getWohnungen();
        BigDecimal count = BigDecimal.valueOf(wohnungen.size());
        if (count.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return wohnungen.stream()
            .map(Wohnung::gesamtKosten)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(count, 2, RoundingMode.HALF_UP);
    }

    public List<Integer> oldestWohnungId() {
        List<Wohnung> wohnungen = hausverwaltungDAO.getWohnungen();

        if (wohnungen.isEmpty()) {
            return new ArrayList<>();
        }

        LocalDate oldestDate = wohnungen.stream()
            .min(Comparator.comparing(Wohnung::getBaujahr))
            .orElseThrow(() -> new AssertionError("unreachable"))
            .getBaujahr();

        return wohnungen.stream()
            .sorted(Comparator.comparing(Wohnung::getBaujahr))
            .filter(w -> oldestDate.equals(w.getBaujahr()))
            .map(Wohnung::getId)
            .collect(Collectors.toList());
    }
}