package jdbreport.model.io.pdf;

import jdbreport.model.io.pdf.itext2.ReportFontMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class ReportFont {

    private static final java.util.List<String> fontPathList = new ArrayList<>();
    private static String defaultFont;
    private static String systemFontPath;

    public static void addFontPath(String path) {
        path = path.trim();
        if (path.length() > 0 && !fontPathList.contains(path)) {
            fontPathList.add(path);
        }
    }

    public static void setFontPaths(Collection<String> fontPaths) {
        fontPathList.clear();
        if (fontPaths != null) {
            fontPaths.forEach(ReportFont::addFontPath);
        }
    }

    public static Collection<String> getFontPaths() {
        if (fontPathList.size() == 0){
            if (systemFontPath == null) {
                systemFontPath = getSystemFontPath();
            }
            if (systemFontPath.length() > 0) {
                fontPathList.add(systemFontPath);
            }
        }
        return Collections.unmodifiableCollection(fontPathList);
    }

    private static String getSystemFontPath() {
        String osName = System.getProperties().getProperty("os.name");
        String path;
        if (osName.startsWith("Windows")) {
            path = System.getenv("SystemRoot");
            if (path != null) {
                path += "\\fonts\\";
            } else {
                path = System.getenv("windir");
                if (path != null) {
                    path += "\\fonts\\";
                } else
                    path = "c:\\windows\\fonts\\";
            }
        }else {
            path = "/usr/share/fonts/";
        }
        File file = new File(path);
        if (file.exists()) {
            return path;
        }
        return "";
    }

    public static String getDefaultFont() {
        return defaultFont;
    }

    public static void setDefaultFont(String fontName) {
        defaultFont = fontName != null && fontName.length() > 0 ? fontName : null;
    }

}
