package thales.spring.angular.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ExempleInjectionCommande {
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("No arguments");
			System.exit(1);
		}

		String cmd = "ls -l " + args[0];
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(new String[] { "sh", "-c", cmd });

		InputStream is = proc.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}
}
