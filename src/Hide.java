import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Hide {
	public static void main(String[] args) throws InterruptedException {
		try {

			File file = new File(".CONFIG_DO_NOT_MODIFY");
			file.setWritable(false);

			setHiddenProperty(file);

			if (file.exists()) {
				System.out.println("Is Execute allow : " + file.canExecute());
				System.out.println("Is Write allow : " + file.canWrite());
				System.out.println("Is Read allow : " + file.canRead());
			}
			file.setWritable(true);
			file.setExecutable(false);
			file.setReadable(false);

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(".CONFIG_DO_NOT_MODIFY"));
			String s1 = "catalog=c:/sri/haimy1";
			bufferedWriter.write(s1);
			bufferedWriter.close();
			Process p = Runtime.getRuntime().exec("attrib +H c:/sri/sri/dcr1.cfg");
			p.waitFor();
			System.out.println("Is Execute allow : " + file.canExecute());
			System.out.println("Is Write allow : " + file.canWrite());
			System.out.println("Is Read allow : " + file.canRead());

			if (file.createNewFile()) {
				System.out.println("File is created!");
			} else {
				System.out.println("File already exists.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void setHiddenProperty(File file) throws InterruptedException, IOException {
		Process p = Runtime.getRuntime().exec("attrib -H " + file.getPath());
		p.waitFor();
	}

}