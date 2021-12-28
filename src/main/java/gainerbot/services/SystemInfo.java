package gainerbot.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO make this more performant. E.g. non-changing Values are queried multiple times.
public class SystemInfo {
    private static final String osName;
    private static final String architecture;
    private static final Runtime runtime;

    private static final double mebiToMegaFactor = 1.049;
    private static final String infoNotAvailable = "Not Available";

    //TODO Add time
    private final String localIP;
    private final String startupTime;
    private final String systemRamUsed;
    private final String systemRamMax;
    private final String javaRamUsed;
    private final String javaRamMax;
    private final String cpuLoad;

    static {
        osName = System.getProperty("os.name");
        architecture = System.getProperty("os.arch");
        runtime = Runtime.getRuntime();
    }

    private SystemInfo(String localIP, String startupTime, String systemRamUsed, String systemRamMax, String javaRamUsed, String javaRamMax, String cpuLoad) {
        this.localIP = localIP;
        this.startupTime = startupTime;
        this.systemRamUsed = systemRamUsed;
        this.systemRamMax = systemRamMax;
        this.javaRamUsed = javaRamUsed;
        this.javaRamMax = javaRamMax;
        this.cpuLoad = cpuLoad;
    }


    public static SystemInfo get(){
        String localIP = getLocalIP();
        String javaRamUsed = getJavaRamUsed();
        String javaRamMax = getJavaRamMax();
        String startupTime;
        String systemRamUsed;
        String systemRamMax;
        String cpuLoad;

        if(isWindows()){
            cpuLoad = getCpuLoad();

            InfoPosition startupPos = new InfoPosition("Systemstartzeit:\\s+(\\d{2}\\.\\d{2}\\.\\d{4}, \\d{2}:\\d{2}:\\d{2})", 1);
            InfoPosition freeRamPos = new InfoPosition("gbarer physischer Speicher:\\s+([\\d\\.]+) MB", 1);
            InfoPosition ramMaxPos = new InfoPosition("Gesamter physischer Speicher:\\s+([\\d\\.]+) MB", 1);
            String[] systeminfos = extractFromSysteminfo(startupPos, ramMaxPos, freeRamPos);

            if(systeminfos == null){
                startupTime = infoNotAvailable;
                systemRamUsed = infoNotAvailable;
                systemRamMax = infoNotAvailable;
            }else{

                if(systeminfos[0] == null){
                    startupTime = infoNotAvailable;
                }else{
                    startupTime = systeminfos[0];
                }

                if(systeminfos[2] == null) {
                    systemRamMax = infoNotAvailable;
                    systemRamUsed = infoNotAvailable;
                }else if(systeminfos[1] == null){
                    systemRamUsed = infoNotAvailable;
                    systemRamMax = systeminfos[2].replaceAll("\\.", "") + " MB";
                }else{
                    long freeRam = Long.parseLong(systeminfos[2].replaceAll("\\.", ""));
                    long maxRam = Long.parseLong(systeminfos[1].replaceAll("\\.", ""));
                    if(maxRam - freeRam < 0){
                        systemRamUsed = infoNotAvailable;
                    }else{
                        systemRamUsed = maxRam-freeRam + " MB";
                    }
                    systemRamMax = maxRam + " MB";
                }
            }
        }else{
            startupTime = getStartupTime();
            cpuLoad = getCpuLoad();

            InfoPosition ramUsedPos = new InfoPosition("Mem:\\s+\\d+\\s+\\d+\\s+(\\d+)\\s+\\d+\\s+\\d+\\s+\\d+", 1);
            InfoPosition ramMaxPos = new InfoPosition("Mem:\\s+(\\d+)\\s+\\d+\\s+\\d+\\s+\\d+\\s+\\d+\\s+\\d+", 1);
            String[] systeminfos = extractFromExec("free -m", ramUsedPos, ramMaxPos);

            if(systeminfos == null){
                systemRamUsed = infoNotAvailable;
                systemRamMax = infoNotAvailable;
            }else{

                if(systeminfos[0] == null){
                    systemRamUsed = infoNotAvailable;
                }else{
                    systemRamUsed = Math.round(Long.parseLong(systeminfos[0]) * mebiToMegaFactor) + " MB";
                }

                if(systeminfos[1] == null){
                    systemRamMax = infoNotAvailable;
                }else{
                    systemRamMax = Math.round(Long.parseLong(systeminfos[1]) * mebiToMegaFactor) + " MB";
                }
            }
        }

        return new SystemInfo(localIP, startupTime, systemRamUsed, systemRamMax, javaRamUsed, javaRamMax, cpuLoad);
    }

    /**
     * Extracts info specifically from the "SYSTEMINFO" command nuder windows.
     * @param positions The InfoPositions to be extracted.
     * @return An array of the found Info. Null if any Error occurred. Values are null if no match occurred.
     */
    private static String[] extractFromSysteminfo(InfoPosition... positions){
        if(positions.length <= 0) return new String[0];
        if(!isWindows()) throw new IllegalStateException("Tried to call \"Systeminfo\" command on a non Windows machine.");
        String[] ret = new String[positions.length];

        String reply;
        try {
            Process p = runtime.exec("SYSTEMINFO");
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line=in.readLine()) != null){
                stringBuilder.append(line);
            }
            reply = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //Match for the info
        int counter = 0;
        for(InfoPosition position : positions){
            Matcher m = Pattern.compile(position.regex).matcher(reply);
            if(m.find()){
                ret[counter] = m.group(position.groupNum);
            }else{
                ret[counter] = null;
            }
            counter++;
        }

        return ret;
    }

    /**
     * Executes a command and searches the Output for given infos.
     * @param command The command to be executed.
     * @param positions The InfoPositions to be extracted
     * @return An array of the found Info. Null if any Error occurred. Values are null if no match occurred.
     */
    private static String[] extractFromExec(String command, InfoPosition... positions){
        if(positions.length <= 0) return new String[0];
        String[] ret = new String[positions.length];

        //Get command output
        String reply;
        try {
            Process p = runtime.exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                stringBuilder.append(line);
            }
            reply = stringBuilder.toString();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }

        //Match for the info
        int counter = 0;
        for(InfoPosition position : positions){
            Matcher m = Pattern.compile(position.regex).matcher(reply);
            if(m.find()){
                ret[counter] = m.group(position.groupNum);
            }else{
                ret[counter] = null;
            }
            counter++;
        }

        return ret;
    }

    public static String getCpuLoad(){
        String command;
        InfoPosition cpuLoadPos;

        if(isWindows()){
            command = "wmic cpu get loadpercentage";
            cpuLoadPos = new InfoPosition("(\\d+)", 1);
        }else{
            command = "uptime";
            cpuLoadPos = new InfoPosition("load average: (\\d+\\.\\d\\d, \\d+\\.\\d\\d, \\d+\\.\\d\\d)", 1);
        }

        String[] result = extractFromExec(command, cpuLoadPos);
        if(result == null || result[0] == null) return infoNotAvailable;

        if(isWindows()){
            return result[0] + "%";
        }else{
            return result[0].replaceFirst(",", "(1min) -").replaceFirst(",", "(5min) -") + "(15min)";
        }
    }

    public static String getSystemRamMax(){
        String command;
        InfoPosition maxRamPos;

        if(isWindows()){
            command = "systeminfo";
            maxRamPos = new InfoPosition("Gesamter physischer Speicher:\\s+([\\d\\.]+) MB", 1);
        }else{
            command = "free -m";
            maxRamPos = new InfoPosition("Mem:\\s+(\\d+)\\s+\\d+\\s+\\d+\\s+\\d+\\s+\\d+\\s+\\d+", 1);
        }

        String[] result = extractFromExec(command, maxRamPos);
        if(result == null || result[0] == null) return infoNotAvailable;

        if(isWindows()){
            return result[0].replaceAll("\\.", "") + " MB";
        }else{
            long usedRam = Math.round(Long.parseLong(result[0]) * mebiToMegaFactor);
            if(usedRam < 0) return infoNotAvailable;
            return usedRam + " MB";
        }
    }

    public static String getSystemRamUsed(){
        String command;

        String[] result;
        if(isWindows()){
            command = "systeminfo";
            InfoPosition freeRamPos = new InfoPosition("Verfügbarer physischer Speicher:\\s+([\\d\\.]+) MB", 1);
            InfoPosition maxRamPos = new InfoPosition("Gesamter physischer Speicher:\\s+([\\d\\.]+) MB", 1);
            result = extractFromExec(command, freeRamPos, maxRamPos);
        }else{
            command = "free -m";
            InfoPosition usedRamPos = new InfoPosition("Mem:\\s+\\d+\\s+\\d+\\s+(\\d+)\\s+\\d+\\s+\\d+\\s+\\d+", 1);
            result = extractFromExec(command, usedRamPos);
        }

        //Make platform-specific adjustments.
        long usedRam;
        if(isWindows()){
            if(result == null || result[0] == null || result[1] == null) return infoNotAvailable;

            long freeRam = Long.parseLong(result[0].replaceAll("\\.", ""));
            long maxRam = Long.parseLong(result[1].replaceAll("\\.", ""));
            usedRam = maxRam - freeRam;
        }else{
            if(result == null || result[0] == null) return infoNotAvailable;

            usedRam = Math.round(Long.parseLong(result[0]) * mebiToMegaFactor);
        }
        if(usedRam < 0) return infoNotAvailable;
        return usedRam + " MB";
    }

    private static String byteToMB(long bytes){
        if(bytes < 1000000){
            return bytes + " Bytes";
        }else{
            double megaBytes = bytes * 0.000001;
            if(bytes < 10000000) return String.format("%f.2", megaBytes) + " MB";
            return Math.round(megaBytes) + " MB";
        }
    }

    public static String getJavaRamMax(){
        long maxMem = runtime.maxMemory();
        if(maxMem == Long.MAX_VALUE) return "∞";
        return byteToMB(maxMem);
    }

    public static String getJavaRamUsed(){
        return byteToMB(runtime.totalMemory());
    }

    public static String getStartupTime(){
        String command;
        InfoPosition startupPos;

        if(isWindows()){
            command = "systeminfo";
            startupPos = new InfoPosition("Systemstartzeit:\\s+(\\d{2}\\.\\d{2}\\.\\d{4}, \\d{2}:\\d{2}:\\d{2})", 1);
        }else{
            command = "uptime -s";
            startupPos = new InfoPosition("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})", 1);
        }

        String[] result = extractFromExec(command, startupPos);
        if(result == null || result[0] == null) return infoNotAvailable;
        return result[0];
    }

    public static String getLocalIP(){
        String command;
        InfoPosition ipPos;

        if(isWindows()){
            command = "ipconfig";
            ipPos = new InfoPosition("IPv4-Adresse  \\. \\. \\. \\. \\. \\. \\. \\. \\. \\. : (192\\.168\\.1\\.\\d{1,3})", 1);
        }else{
            command = "ip address";
            ipPos = new InfoPosition("inet (192\\.168\\.1\\.\\d{1,3})\\/24", 1);
        }

        String[] result = extractFromExec(command, ipPos);
        if(result == null || result[0] == null) return infoNotAvailable;
        return result[0];
    }

    public static boolean isWindows(){
        return osName.startsWith("Windows");
    }


    //region Getters
    public static String getOsName(){
        return osName;
    }

    public static String getArchitecture(){
        return architecture;
    }

    public String getCachedStartupTime() {
        return startupTime;
    }

    public String getCachedJavaRamMax(){
        return javaRamMax;
    }

    public String getCachedJavaRamUsed(){
        return javaRamUsed;
    }

    public String getCachedSystemRamUsed() {
        return systemRamUsed;
    }

    public String getCachedSystemRamMax() {
        return systemRamMax;
    }

    public String getCachedCpuLoad() {
        return cpuLoad;
    }

    public String getCachedLocalIP(){
        return localIP;
    }
    //endregion

    private static class InfoPosition{
        private final String regex;
        private final int groupNum;

        private InfoPosition(String regex, int groupNum){
            this.regex = regex;
            this.groupNum = groupNum;
        }
    }
}
