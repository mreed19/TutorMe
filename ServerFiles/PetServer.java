
import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
import java.io.*;
import java.util.*;


///////////////////////////// Mutlithreaded Server /////////////////////////////

public class PetServer
{
   final static int port = 3300;

   static void printUsage() {
    System.out.println("In another window type:");
    System.out.println("telnet data.cs.purdue.edu " + port);
 System.out.println("GET-ALL-TUTORS|user|password");
   }

   public static void main(String[] args )
   {  
      try
      {  
         printUsage();
         int i = 1;
         ServerSocket s = new ServerSocket(port);
  while (true)
         {  
            Socket incoming = s.accept();
            System.out.println("Spawning " + i);
            Runnable r = new ThreadedHandler(incoming);
            Thread t = new Thread(r);
            t.start();
            i++;
         }
      }
      catch (IOException e)
      {  
         e.printStackTrace();
      }
   }
}

/**
   This class handles the client input for one server socket connection. 
*/
class ThreadedHandler implements Runnable
{ 
   final static String ServerUser = "root";
   final static String ServerPassword = "sae91229122";

   public ThreadedHandler(Socket i)
   { 
      incoming = i; 
   }

   public static Connection getConnection() throws SQLException, IOException
   {
      Properties props = new Properties();
      FileInputStream in = new FileInputStream("database.properties");
      props.load(in);
      in.close();
      String drivers = props.getProperty("jdbc.drivers");
      if (drivers != null)
        System.setProperty("jdbc.drivers", drivers);
      String url = props.getProperty("jdbc.url");
      String username = props.getProperty("jdbc.username");
      String password = props.getProperty("jdbc.password");

      System.out.println("url="+url+" user="+username+" password="+password);

      return DriverManager.getConnection( url, username, password);
   }


   void getAllTutors(String[] args, PrintWriter out) {
    Connection conn = null;
 try {
  conn = getConnection();
  Statement stat = conn.createStatement();
  ResultSet result = stat.executeQuery("SELECT * FROM tutor ORDER BY subject ASC, name ASC");

  while(result.next()) {
   for (int i = 1; i <= 7; i++)
    out.print(result.getString(i)+"|");
   out.println("");
  }

  result.close();
 }
 catch (Exception e) {
  System.out.println(e.toString());
  out.println(e.toString());
 }
 finally {
  try {
   if(conn!=null) conn.close();
  }
  catch (Exception e){
  } 
 }
   }
    
   
   void getAllPets( String [] args, PrintWriter out) {

      Connection conn=null;
      try
      {
 conn = getConnection();
        Statement stat = conn.createStatement();
 
 ResultSet result = stat.executeQuery( "SELECT * FROM pet");

 while(result.next()) {
         out.print(result.getString(1)+"|");
         out.print(result.getString(2)+"|");
         out.print(result.getString(3)+"|");
         out.print(result.getString(4)+"|");
         out.print(result.getString(5));
  out.println("");
 }

 result.close();

 /*
  stat.executeUpdate(
           "CREATE TABLE Greetings (Message CHAR(20))");
        stat.executeUpdate(
           "INSERT INTO Greetings VALUES ('Hello, World!')");
 ResultSet result = 
            stat.executeQuery(
               "SELECT * FROM Greetings");
         while(result.next())
            System.out.println(result.getString(1));
         result.close();
         stat.executeUpdate("DROP TABLE Greetings");
 */

      }
      catch (Exception e) {
 System.out.println(e.toString());
 out.println(e.toString());
      }
      finally
      {
 try {
         if (conn!=null) conn.close();
 }
 catch (Exception e) {
 }
      }
   }
   
   void getTutorsByCourse( String[] args, PrintWriter out) {
    Connection conn = null;
 try {
  conn = getConnection();
  PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tutor WHERE subject=? AND course=? ORDER BY name ASC");
  pstmt.setString(1, args[3]);
  pstmt.setString(2, args[4]);
  ResultSet result = pstmt.executeQuery();
  
  if (!result.isBeforeFirst()) {
   out.println("No Results Found.");
  }
  else {
   while(result.next()) {
    for (int i = 1; i <= 7; i++)
     out.print(result.getString(i)+"|");
    out.println("");
   }
  }
  result.close();
 } catch (Exception e) {
  System.out.println(e.toString());
  out.println(e.toString());
 } finally {
  try {
   if (conn != null) conn.close();
  } catch (Exception e) {
  }
 }
   }

   void addActive(String[] args, PrintWriter out) {
    Connection conn=null;
 try {
  conn=getConnection();
  PreparedStatement pstmt = conn.prepareStatement("INSERT INTO actives VALUES(?,?,?,?,?)");
  for (int i = 1; i <= 5; i++)
   pstmt.setString(i, args[i+2]);
  int returnValue = pstmt.executeUpdate();
  if (returnValue == 0)
   out.println("Error adding active tutor");
  else
   out.println("You are now listed as an active tutor.");
 }
 catch (Exception e) {
  System.out.println(e.toString());
  out.println(e.toString());
 }
 finally {
  try {
   if (conn != null) conn.close();
  } catch (Exception e) {
  }
 }
   }
   
   void removeActive(String[] args, PrintWriter out) {
   	Connection conn=null;
	try {
		conn=getConnection();
		PreparedStatement pstmt = conn.prepareStatement("DELETE FROM actives WHERE name=? AND number=? AND subject=? AND course=? AND location=?");
		for (int i = 1; i <= 5; i++)
	 		pstmt.setString(i, args[i+2]);
		int returnValue = pstmt.executeUpdate();
		if (returnValue == 0)
	 		out.println("Error removing active tutor");
		else
	 		out.println("You are no longer an active tutor.");
	}
	catch (Exception e) {
		System.out.println(e.toString());
		out.println(e.toString());
	}
	finally {
		try {
			if (conn != null) conn.close();
		} catch (Exception e) {
		}
	}
   }


   void addTutor(String[] args, PrintWriter out) {
    Connection conn=null;
 try {
  conn=getConnection();
  PreparedStatement pstmt = conn.prepareStatement("INSERT INTO tutor VALUES(?,?,?,?,?,?,?)");
  for (int i = 1; i <= 7; i++)
   pstmt.setString(i, args[i+2]);
  int returnValue = pstmt.executeUpdate();
  if (returnValue == 0)
   out.println("Error adding Tutor.");
  else
   out.println("Successfully add a new Tutor!");
 }
 catch (Exception e) {
  System.out.println(e.toString());
  out.println(e.toString());
 }
 finally {
  try{
   if (conn != null) conn.close();
  }
  catch (Exception e) {
  }
 }
   }

   void getActives(String[] args, PrintWriter out) {
   	Connection conn = null;
	try {
		conn = getConnection();
		PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM actives WHERE subject=? AND course=? ORDER BY name ASC");
		pstmt.setString(1, args[3]);
		pstmt.setString(2, args[4]);
		ResultSet result = pstmt.executeQuery();
		if (!result.isBeforeFirst()) {
			out.println("No Results Found.");
		}
		else {
			while(result.next()) {
				for (int i = 1; i <= 5; i++)
					out.print(result.getString(i) + "|");
				out.println("");
			}
		}
		result.close();
	}
	catch (Exception e) {
		System.out.println(e.toString());
		out.println(e.toString());
	}
	finally {
		try {
			if (conn != null) conn.close();
		} catch (Exception e) {
		}
	}
   }
   
   void getTutorsBySubject( String [] args, PrintWriter out) {

    Connection conn=null;
 try {
  conn = getConnection();
  PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM tutor WHERE subject=? ORDER BY name ASC");
  pstmt.setString(1, args[3]);
  ResultSet result = pstmt.executeQuery();

  if (!result.isBeforeFirst()) {
   out.println("No Results Found.");
  }
  else {
   while(result.next()) {
    for (int i = 1; i <= 7; i++)
     out.print(result.getString(i)+"|");
    out.println("");
   }
  }
  result.close();
 }
 catch (Exception e) {
  System.out.println(e.toString());
  out.println(e.toString());
 }
 finally {
  try {
   if (conn != null) conn.close();
  } catch (Exception e) {
  }
 }
   }

   void getPetInfo( String [] args, PrintWriter out) {

      Connection conn=null;
      try
      {
 conn = getConnection();

 PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM pet WHERE name LIKE ?");
 pstmt.setString(1, args[3]); 
 ResultSet result = pstmt.executeQuery();

 while(result.next()) {
         out.print(result.getString(1)+"|");
         out.print(result.getString(2)+"|");
         out.print(result.getString(3)+"|");
         out.print(result.getString(4)+"|");
         out.print(result.getString(5));
  out.println("");
 }

 result.close();
      }
      catch (Exception e) {
 System.out.println(e.toString());
 out.println(e.toString());
      }
      finally
      {
 try {
         if (conn!=null) conn.close();
 }
 catch (Exception e) {
 }
      }
   }   

   void handleRequest( InputStream inStream, OutputStream outStream) {
        Scanner in = new Scanner(inStream);         
        PrintWriter out = new PrintWriter(outStream, 
                                      true /* autoFlush */);

 // Get parameters of the call
 String request = in.nextLine();

 System.out.println("Request="+request);

 String requestSyntax = "Syntax: COMMAND|USER|PASSWORD|OTHER|ARGS";

 try {
  // Get arguments.
  // The format is COMMAND|USER|PASSWORD|OTHER|ARGS...
  String [] args = request.split("\\|");

  // Print arguments
  for (int i = 0; i < args.length; i++) {
   System.out.println("Arg "+i+": "+args[i]);
  }

  // Get command and password
  String command = args[0];
  String user = args[1];
  String password = args[2];

  // Check user and password. Now it is sent in plain text.
  // You should use Secure Sockets (SSL) for a production environment.
  if ( !user.equals(ServerUser) || !password.equals(ServerPassword)) {
   System.out.println("Bad user or password");
   out.println("Bad user or password");
   return;
  }

  // Do the operation
  if (command.equals("GET-ALL-PETS")) {
   getAllPets(args, out);
  }
  else if (command.equals("GET-PET-INFO")) {
   getPetInfo(args, out);
  }
  else if (command.equals("GET-ALL-TUTORS")) {
   getAllTutors(args, out);
  }
  else if (command.equals("GET-TUTORS-BY-SUBJECT")) {
   getTutorsBySubject(args, out);
  }
  else if (command.equals("GET-TUTORS-BY-COURSE")) {
   getTutorsByCourse(args, out);
  }
  else if (command.equals("ADD-TUTOR")) {
   addTutor(args, out);
  }
  else if (command.equals("ADD-ACTIVE")) {
   addActive(args, out);
  }
  else if (command.equals("REMOVE-ACTIVE")) {
   removeActive(args, out);
  }
  else if (command.equals("GET-ACTIVES")) {
   getActives(args, out);
  }
 }
 catch (Exception e) {  
  System.out.println(requestSyntax);
  out.println(requestSyntax);

  System.out.println(e.toString());
  out.println(e.toString());
 }
   }

   public void run() {  
      try
      {  
         try
         {
            InputStream inStream = incoming.getInputStream();
            OutputStream outStream = incoming.getOutputStream();
     handleRequest(inStream, outStream);

         }
        catch (IOException e)
         {  
            e.printStackTrace();
         }
         finally
         {
            incoming.close();
         }
      }
      catch (IOException e)
      {  
         e.printStackTrace();
      }
   }

   private Socket incoming;
}

