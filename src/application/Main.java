package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	Socket socket;
	TextArea textArea;

	// Client 프로그램 작동 시작
	public void startClient(String IP, int port) {
		// 클라이언트에서는 Threadpool 사용 필요 X
		Thread thread = new Thread() {
			public void run() {
				try {
					socket = new Socket(IP, port);
					receive();
				} catch (Exception e) {
					if (!socket.isClosed()) {
						stopClient();
						System.out.println("[서버 접속 실패 : ]" + e.getStackTrace());
						Platform.exit();
					}
				}
			}
		};
		thread.start();
	}

	// Client 프로그램 작동 종료
	public void stopClient() {
		try{
			if(socket != null && !socket.isClosed()){
				socket.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 서버로 메시지 전달 받음
	public void receive() {
		while(true){
			try {
				InputStream in = socket.getInputStream();
				byte[] buffer = new byte[512];
				int length = in.read(buffer);
				if(length == -1){
					throw new IOException();
				}
				String message = new String(buffer, 0, length, "UTF-8");
				Platform.runLater(() -> {
					textArea.appendText(message);
				});
			} catch (Exception e) {
				stopClient();
				System.out.println("[메시지 전달 에러] : " + e.getStackTrace());
				break;
			}
		}
	}

	// 서버로 메시지 전송
	public void send(String message) {
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				} catch (Exception e) {
					stopClient();
					System.out.println("[메시지 전송 에러] : " + e.getStackTrace());
				}
			}
		};
		thread.start();
	}

	// 실제로 프로그램 작동
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene =  new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toString());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 프로그램 진입점
	public static void main(String[] args) {
		// TODO 자동 생성된 메소드 스텁
		launch(args);

	}

}
