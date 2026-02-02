package pl.projekt.tennis_ranking.service;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.projekt.tennis_ranking.model.Mecz;
import pl.projekt.tennis_ranking.model.PunktyLog;
import pl.projekt.tennis_ranking.model.Turniej;
import pl.projekt.tennis_ranking.model.ZgloszenieTurniejowe;
import pl.projekt.tennis_ranking.repo.MeczRepository;
import pl.projekt.tennis_ranking.repo.PunktyLogRepository;
import pl.projekt.tennis_ranking.repo.TurniejRepository;
import pl.projekt.tennis_ranking.repo.ZawodnikRepository;
import pl.projekt.tennis_ranking.repo.ZgloszenieTurniejoweRepository;

@Service
public class TournamentReportService {

    private final TurniejRepository turniejRepo;
    private final ZgloszenieTurniejoweRepository zglRepo;
    private final MeczRepository meczRepo;
    private final ZawodnikRepository zawodnikRepo;
    private final PunktyLogRepository punktyLogRepo;
    private final TournamentService tournamentService;

    public TournamentReportService(
            TurniejRepository turniejRepo,
            ZgloszenieTurniejoweRepository zglRepo,
            MeczRepository meczRepo,
            ZawodnikRepository zawodnikRepo,
            PunktyLogRepository punktyLogRepo,
            TournamentService tournamentService
    ) {
        this.turniejRepo = turniejRepo;
        this.zglRepo = zglRepo;
        this.meczRepo = meczRepo;
        this.zawodnikRepo = zawodnikRepo;
        this.punktyLogRepo = punktyLogRepo;
        this.tournamentService = tournamentService;
    }

    @Transactional(readOnly = true)
    public byte[] buildXlsx(String idTurnieju) {
        Turniej t = turniejRepo.findById(idTurnieju)
                .orElseThrow(() -> new IllegalArgumentException("Nie ma turnieju: " + idTurnieju));

        List<ZgloszenieTurniejowe> zgl = zglRepo.findByIdTurnieju(idTurnieju);
        List<Mecz> mecze = meczRepo.findByIdTurniejuOrderByRundaAscSlotWRundzieAsc(idTurnieju);
        List<PunktyLog> logs = punktyLogRepo.findByIdTurniejuOrderByCreatedAtAsc(idTurnieju);

        // mapy nazw
        Set<String> ids = new HashSet<>();
        for (ZgloszenieTurniejowe z : zgl) ids.add(z.getIdZawodnika());
        for (Mecz m : mecze) {
            if (m.getIdZawodnikA() != null) ids.add(m.getIdZawodnikA());
            if (m.getIdZawodnikB() != null) ids.add(m.getIdZawodnikB());
            if (m.getIdZwyciezcy() != null) ids.add(m.getIdZwyciezcy());
        }
        for (PunktyLog pl : logs) ids.add(pl.getIdZawodnika());

        Map<String, String> labels = tournamentService.mapZawodnikIdToLabel(ids);

        // suma punktów z logów
        Map<String, Integer> sumByPlayer = logs.stream()
                .collect(Collectors.groupingBy(PunktyLog::getIdZawodnika, Collectors.summingInt(PunktyLog::getPunkty)));

        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle header = headerStyle(wb);

            // ===== Sheet 1: Turniej =====
            Sheet s1 = wb.createSheet("Turniej");
            int r = 0;
            r = kvRow(s1, r, header, "ID", t.getIdTurnieju());
            r = kvRow(s1, r, header, "Nazwa", t.getNazwa());
            r = kvRow(s1, r, header, "Ranga", t.getRanga());
            r = kvRow(s1, r, header, "Sezon", String.valueOf(t.getSezon()));
            r = kvRow(s1, r, header, "Status", t.getStatus() != null ? t.getStatus().name() : "-");
            r = kvRow(s1, r, header, "Max zawodników", String.valueOf(t.getMaxZawodnikow()));
            s1.autoSizeColumn(0);
            s1.autoSizeColumn(1);

            // ===== Sheet 2: Zgłoszenia =====
            Sheet s2 = wb.createSheet("Zgloszenia");
            Row h2 = s2.createRow(0);
            h2.createCell(0).setCellValue("Zawodnik");
            h2.createCell(1).setCellValue("ID zawodnika");
            h2.createCell(2).setCellValue("Status");
            applyHeader(h2, header);

            int rr = 1;
            for (ZgloszenieTurniejowe zg : zgl) {
                Row row = s2.createRow(rr++);
                String zid = zg.getIdZawodnika();
                row.createCell(0).setCellValue(labels.getOrDefault(zid, zid));
                row.createCell(1).setCellValue(zid);
                row.createCell(2).setCellValue(zg.getStatus() != null ? zg.getStatus().name() : "-");
            }
            for (int c = 0; c < 3; c++) s2.autoSizeColumn(c);

            // ===== Sheet 3: Drabinka =====
            Sheet s3 = wb.createSheet("Drabinka");
            Row h3 = s3.createRow(0);
            h3.createCell(0).setCellValue("Runda");
            h3.createCell(1).setCellValue("Slot");
            h3.createCell(2).setCellValue("Zawodnik A");
            h3.createCell(3).setCellValue("Zawodnik B");
            h3.createCell(4).setCellValue("Wynik");
            h3.createCell(5).setCellValue("Zwyciezca");
            h3.createCell(6).setCellValue("Status");
            applyHeader(h3, header);

            rr = 1;
            for (Mecz m : mecze) {
                Row row = s3.createRow(rr++);
                row.createCell(0).setCellValue(m.getRunda());
                row.createCell(1).setCellValue(m.getSlotWRundzie());
                row.createCell(2).setCellValue(safeLabel(labels, m.getIdZawodnikA(), "BYE"));
                row.createCell(3).setCellValue(safeLabel(labels, m.getIdZawodnikB(), "BYE"));
                row.createCell(4).setCellValue(m.getWynik() != null ? m.getWynik() : "");
                row.createCell(5).setCellValue(safeLabel(labels, m.getIdZwyciezcy(), ""));
                row.createCell(6).setCellValue(m.getStatus() != null ? m.getStatus().name() : "");
            }
            for (int c = 0; c < 7; c++) s3.autoSizeColumn(c);

            // ===== Sheet 4: Punkty (SUMA) =====
            Sheet s4 = wb.createSheet("Punkty");
            Row h4 = s4.createRow(0);
            h4.createCell(0).setCellValue("Zawodnik");
            h4.createCell(1).setCellValue("ID zawodnika");
            h4.createCell(2).setCellValue("Punkty w tym turnieju (SUMA z logów)");
            applyHeader(h4, header);

            rr = 1;
            List<Map.Entry<String, Integer>> pts = sumByPlayer.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .toList();

            for (var e : pts) {
                Row row = s4.createRow(rr++);
                String zid = e.getKey();
                row.createCell(0).setCellValue(labels.getOrDefault(zid, zid));
                row.createCell(1).setCellValue(zid);
                row.createCell(2).setCellValue(e.getValue());
            }
            for (int c = 0; c < 3; c++) s4.autoSizeColumn(c);

            // ===== Sheet 5: Punkty_log (szczegóły) =====
            Sheet s5 = wb.createSheet("Punkty_log");
            Row h5 = s5.createRow(0);
            h5.createCell(0).setCellValue("Czas");
            h5.createCell(1).setCellValue("Zawodnik");
            h5.createCell(2).setCellValue("ID zawodnika");
            h5.createCell(3).setCellValue("ID meczu");
            h5.createCell(4).setCellValue("Typ");
            h5.createCell(5).setCellValue("Punkty");
            applyHeader(h5, header);

            rr = 1;
            for (PunktyLog pl : logs) {
                Row row = s5.createRow(rr++);
                row.createCell(0).setCellValue(pl.getCreatedAt() != null ? pl.getCreatedAt().toString() : "");
                row.createCell(1).setCellValue(labels.getOrDefault(pl.getIdZawodnika(), pl.getIdZawodnika()));
                row.createCell(2).setCellValue(pl.getIdZawodnika());
                row.createCell(3).setCellValue(pl.getIdMeczu());
                row.createCell(4).setCellValue(pl.getTyp());
                row.createCell(5).setCellValue(pl.getPunkty());
            }
            for (int c = 0; c < 6; c++) s5.autoSizeColumn(c);

            wb.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Nie udało się wygenerować XLSX: " + ex.getMessage(), ex);
        }
    }

    private static String safeLabel(Map<String, String> labels, String id, String fallback) {
        if (id == null || id.isBlank()) return fallback;
        return labels.getOrDefault(id, fallback);
    }

    private CellStyle headerStyle(Workbook wb) {
        Font f = wb.createFont();
        f.setBold(true);
        CellStyle s = wb.createCellStyle();
        s.setFont(f);
        return s;
    }

    private void applyHeader(Row row, CellStyle header) {
        for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) row.getCell(i).setCellStyle(header);
    }

    private int kvRow(Sheet s, int r, CellStyle header, String k, String v) {
        Row row = s.createRow(r++);
        Cell c0 = row.createCell(0);
        c0.setCellValue(k);
        c0.setCellStyle(header);
        row.createCell(1).setCellValue(v != null ? v : "");
        return r;
    }
}
