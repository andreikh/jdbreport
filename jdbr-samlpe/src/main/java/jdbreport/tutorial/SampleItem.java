package jdbreport.tutorial;

import java.net.URL;

public abstract class SampleItem {

	public abstract void run();

	public abstract String getTemplate();

	public abstract String getCaption();

	private String getTemplatePath() {
		return "/tutorial/template/";
	}

	private String getSourcePath() {
		return "/jdbreport/tutorial/";
	}

	public String getSource() {
		return getSourcePath() + getClass().getSimpleName() + ".java";
	}

	public URL getTemplateURL() {
		return getClass().getResource(getTemplatePath() + getTemplate());
	}

	public String toString() {
		return getCaption();
	}
}
