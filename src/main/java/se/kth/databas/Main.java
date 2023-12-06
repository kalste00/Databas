package se.kth.databas;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import se.kth.databas.model.BooksDbInterface;
import se.kth.databas.model.BooksDbException;
import se.kth.databas.model.BooksDbMockImpl;
import se.kth.databas.view.BooksPane;

/**
 * Application start up.
 *
 * @author anderslm@kth.se
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            BooksDbMockImpl booksDb = new BooksDbMockImpl();
            if (booksDb.connect("kcdb")) {
                BooksPane root = new BooksPane(booksDb);
                Scene scene = new Scene(root, 800, 600);

                primaryStage.setTitle("Books Database Client");
                primaryStage.setOnCloseRequest(event -> {
                    try {
                        booksDb.disconnect();
                    } catch (BooksDbException e) {
                        e.printStackTrace();
                    }
                });

                primaryStage.setScene(scene);
                primaryStage.show();
            } else {
                System.out.println("Failed to connect to the database.");
            }
        } catch (BooksDbException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
