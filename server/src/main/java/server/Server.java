package server;

import com.google.gson.Gson;
import model.UserData;
import spark.*;

public class Server {
    private Gson gson = new Gson();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (req, res) -> {
            return registerUser(req.body());
        });
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String registerUser(String requestBody) {
        handler.UserHandler.registerUser(requestBody);
        return "User is now registered!";
    }

}
