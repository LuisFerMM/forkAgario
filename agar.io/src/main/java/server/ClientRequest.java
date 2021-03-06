package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientRequest extends Thread {

	private Socket socket;
	private Server server;

	public ClientRequest(Socket socket, Server server) {

		this.socket = socket;
		this.server = server;
	}

	public void run() {

		// le quite el while true
		handleRequest(this.socket);

	}

	private void handleRequest(Socket socket2) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String headerLine = in.readLine();
			// A tokenizer is a process that splits text into a series of tokens
			StringTokenizer tokenizer = new StringTokenizer(headerLine);
			// The nextToken method will return the next available token
			String httpMethod = tokenizer.nextToken();
			// The next code sequence handles the GET method. A message is displayed on the
			// server side to indicate that a GET method is being processed
			if (httpMethod.equals("GET")) {
				System.out.println("Get method processed");
				String httpQueryString = tokenizer.nextToken();
				System.out.println(httpQueryString);
				if (httpQueryString.equals("/")) {
					StringBuilder responseBuffer = new StringBuilder();
					String str = "";
					BufferedReader buf = new BufferedReader(new FileReader("web/AppWeb/javascr.html"));
					while ((str = buf.readLine()) != null) {
						responseBuffer.append(str);
					}
					sendResponse(socket, 200, responseBuffer.toString());
					buf.close();
				}
				// permite obtener el dato ingresado en el submit
				String[] response = httpQueryString.split("=");
				if (response.length==3) {
					String user = response[1].split("&")[0];
					String password = response[2].split("&")[0];
					if (server.validateUser(user, password)) {
						System.out.println("Get method processed");
						String mensajeObtenido = server.requestDB(response[1]);
						String[] lista = mensajeObtenido.split("\n");
						StringBuilder responseBuffer = new StringBuilder();
						responseBuffer.append("<html>").append("<head>").append("<style>").append("body{").append(
								"	background-image: url(\"http://img01.deviantart.net/4b24/i/2015/160/3/2/agar_io_by_alexia_way-d8wo9zt.jpg\");")
						.append("table td:hover{").append("background: #000; color:white;}")
						.append("table tr:hover{ background: #ccc;}"
								+ "table {width: 100%; border-collapse: collapse;}")
						.append("th, td {border: 1px solid #000;"
								+ "background: #eee; text-align: center;"
								+ "vertical-align: center;}")
								.append("}").append("</style>")
								.append("<title>Informacion Nickname Correspondiente</title>").append("</head>")
								.append("<body>").append("<h1>Partidas jugadas</h1>").append("<table>").append("<tr>")
								.append("<td><strong>Nickname</strong></td>")
								.append("<td><strong>You won?</strong></td>").append("<td><strong>Date</strong></td>")
								.append("<td><strong>Score</strong></td>").append("<td><strong>Enemies</strong></td>");
						agregarlista(lista, responseBuffer, user);
						responseBuffer.append("</table>").append("</body>").append("</html>");

						sendResponse(socket, 200, responseBuffer.toString());
					} else {
						sendResponse(socket, 400, "User or password wrong");
					}
				}
			} else {
				System.out.println("The HTTP method is not recognized");
				sendResponse(socket, 405, "Method Not Allowed");
			}

		}catch(

	IOException e)
	{
		e.printStackTrace();
	}

	}

	private void agregarlista(String[] lista, StringBuilder responseBuffer, String parameter) {
		parameter = parameter.split("&")[0];
		for (int i = 0; i < lista.length; i++) {
			System.out.println("Busca en DB :" + lista[i]);
			if (lista[i].split(",")[0].compareToIgnoreCase(parameter) == 0
					|| lista[i].split(",")[6].compareToIgnoreCase(parameter) == 0) {
				responseBuffer.append("<tr>");
				responseBuffer.append("<td>" + lista[i].split(",")[0] + "</td>");
				responseBuffer.append("<td>" + lista[i].split(",")[2] + "</td>");
				responseBuffer.append("<td>" + lista[i].split(",")[3] + "</td>");
				responseBuffer.append("<td>" + lista[i].split(",")[4] + "</td>");
				responseBuffer.append("<td>" + lista[i].split(",")[5] + "</td>");
				responseBuffer.append("<tr>");
			}

		}

	}

	public void sendResponse(Socket socket, int statusCode, String responseString) {
		String statusLine;
		String serverHeader = "Server: WebServer\r\n";
		String contentTypeHeader = "Content-Type: text/html\r\n";

		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			if (statusCode == 200) {
				statusLine = "HTTP/1.0 200 OK" + "\r\n";
				String contentLengthHeader = "Content-Length: " + responseString.length() + "\r\n";
				out.writeBytes(statusLine);
				out.writeBytes(serverHeader);
				out.writeBytes(contentTypeHeader);
				out.writeBytes(contentLengthHeader);
				out.writeBytes("\r\n");
				out.writeBytes(responseString);
			} else if (statusCode == 405) {
				statusLine = "HTTP/1.0 405 Method Not Allowed" + "\r\n";
				out.writeBytes(statusLine);
				out.writeBytes("\r\n");
			} else if (statusCode == 400) {
				statusLine = "HTTP/1.0 400 Wrong email or password" + "\r\n";
				out.writeBytes(statusLine);
				out.writeBytes("\r\n");
			} else {
				statusLine = "HTTP/1.0 404 Not Found" + "\r\n";
				out.writeBytes(statusLine);
				out.writeBytes("\r\n");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
