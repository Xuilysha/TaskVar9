package ru.university.task9;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.university.task9.controller.TaskServlet;
import ru.university.task9.repository.ScenarioRepository;
import ru.university.task9.service.SimService;

public class Main {
    public static void main(String[] args) throws Exception {
        String url = env("DB_URL", "jdbc:postgresql://localhost:5432/taskvar9");
        String user = env("DB_USER", "postgres");
        String pass = env("DB_PASSWORD", "");
        int port = Integer.parseInt(env("PORT", "8080"));

        ScenarioRepository repo = new ScenarioRepository(url, user, pass);
        repo.init();

        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setHost("127.0.0.1");
        connector.setPort(port);
        server.addConnector(connector);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new TaskServlet(new SimService(repo))), "/*");
        server.setHandler(context);
        server.start();
        System.out.println("Откройте http://localhost:" + port);
        server.join();
    }

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isEmpty() ? defaultValue : value;
    }
}
