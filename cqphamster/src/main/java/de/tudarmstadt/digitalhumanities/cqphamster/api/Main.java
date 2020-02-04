package de.tudarmstadt.digitalhumanities.cqphamster.api;

import java.io.IOException;
import java.util.Collections;
import java.util.Scanner;

import org.apache.ignite.Ignition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.digitalhumanities.cqphamster.core.TransactionManager;
import de.tudarmstadt.digitalhumanities.cqphamster.model.User;
import de.tudarmstadt.digitalhumanities.cqphamster.util.Utils;

@SpringBootApplication
public class Main {
	
	private static User getStandardUser() {
		User superuser = new User();
		superuser.setFullName("Superuser");
		superuser.setRole("admin");
		superuser.setAdmin(true);
		superuser.setMayManageCorpora(true);
		
		System.out.println("No superuser detected in currently connected Ignite Cluster. Please enter Information for a new one.");
		System.out.println("Please enter the respective eMail adress.");
		
		Scanner scanner = new Scanner(System.in);
		
		superuser.seteMail(scanner.next());
		
		scanner.close();
		System.out.println("Superuser is going to be created.");
		
		return superuser;
	}
	
	public static void main(String...args) throws IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		Utils.createStandardConfigurationIfNotFound();
		
		DKProComponentDetector.detectDKProComponents();
		DKProComponentDetector.updateAnnotatorLists();
		
		TransactionManager man = new TransactionManager();
		
		if (man.getObjectById(User.class.getCanonicalName(), 0) == null)
			man.addOrUpdateObject(getStandardUser());
		
		SpringApplication app = new SpringApplication(Main.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", Utils.getConfigurationValue("port")));
		
		app.run(args);
		
	}
}
