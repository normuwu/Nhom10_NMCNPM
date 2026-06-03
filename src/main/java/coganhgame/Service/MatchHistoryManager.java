package coganhgame.Service;

import coganhgame.Model.Game.MatchRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** Reads / writes match history to match_history.txt (pipe-delimited format). */
public class MatchHistoryManager {
    private static final String FILE_NAME = "match_history.txt";

    /** Append a match record */
    public static void saveRecord(MatchRecord record) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_NAME, true))) {
            out.println(record.toCsvLine());
        } catch (IOException e) {
            System.err.println("Failed to save match: " + e.getMessage());
        }
    }

    /** Load all records (newest first) */
    public static List<MatchRecord> loadAll() {
        List<MatchRecord> records = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return records;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!(line = line.trim()).isEmpty()) records.add(new MatchRecord(line));
            }
        } catch (IOException e) {
            System.err.println("Failed to load matches: " + e.getMessage());
        }
        // Reverse so newest first
        List<MatchRecord> reversed = new ArrayList<>();
        for (int i = records.size() - 1; i >= 0; i--) reversed.add(records.get(i));
        return reversed;
    }

    public static void clearAll() {
        try (PrintWriter out = new PrintWriter(FILE_NAME)) { out.print(""); }
        catch (IOException e) { System.err.println("Failed to clear: " + e.getMessage()); }
    }
}
         