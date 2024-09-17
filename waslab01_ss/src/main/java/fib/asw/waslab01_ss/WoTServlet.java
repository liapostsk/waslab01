package fib.asw.waslab01_ss;

import java.io.*;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(value = "/")
public class WoTServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private TweetDAO tweetDAO;
	private Locale currentLocale = new Locale("en");
    private String ENCODING = "ISO-8859-1";

    public void init() {
    	tweetDAO = new TweetDAO((java.sql.Connection) this.getServletContext().getAttribute("connection"));
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
    	List<Tweet> tweets = tweetDAO.getAllTweets();
    	
        if (request.getHeader("Accept").equals("text/plain")) printPLAINresult(response, tweets);
        else {
            printHTMLresults(response, tweets);
        }
    }

    private void printPLAINresult(HttpServletResponse response, List<Tweet> tweets) throws IOException {
    	response.setContentType ("text/plain");
        response.setCharacterEncoding(ENCODING);
        PrintWriter out = response.getWriter();
        
        for (Tweet tweet: tweets) {

            out.println(tweet.getCreated_at() + " (tweet.id = " + tweet.getTwid() + "): " + tweet.getAuthor() + " wrote \"" + tweet.getText() + "\"");
        }
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String author = new String();
		String tweetText = new String();
		
		String wid = request.getParameter("twid"); //Id del tweet que se desea eliminar, proporcionado por el form HTML
		
		response.setContentType ("text/plain");
        response.setCharacterEncoding(ENCODING);
        PrintWriter out = response.getWriter();
		
		long id = 0; //id que la base de dades asigna a un tweet
	
		if (wid != null) {
			
			Cookie[] vectorCookies = request.getCookies();
			
			if(vectorCookies != null) 
			{
			
				for(Cookie cookies:vectorCookies)
				{
	
					if(cookies.getValue().equals(sha256(wid))) 
					{
						tweetDAO.deleteTweet(Integer.parseInt(wid));
					}
				}
		
			}
		}
		else {
			author = request.getParameter("author");
			tweetText = request.getParameter("tweet_text");
			
			try {
				id = tweetDAO.insertTweet(author, tweetText);
				Cookie c = new Cookie("idTweetClient:" + String.valueOf(id), sha256(String.valueOf(id)));
			    response.addCookie(c);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		
		if (request.getHeader("Accept").equals("text/plain")) out.println(id);
        else {
        	// This method does NOTHING but redirect to the main page
        	response.sendRedirect(request.getContextPath());
        }
        
    }

    private void printHTMLresults (HttpServletResponse response, List<Tweet> tweets) throws IOException {
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, currentLocale);
        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT, currentLocale);
        response.setContentType ("text/html");
        response.setCharacterEncoding(ENCODING);

        PrintWriter out = response.getWriter();


        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Wall of Tweets</title>");
        out.println("<link href=\"wot.css\" rel=\"stylesheet\" type=\"text/css\" />");
        out.println("</head>");
        out.println("<body class=\"wallbody\">");
        out.println("<h1>Wall of Tweets</h1>");
        out.println("<div class=\"walltweet\">");
        out.println("<form method=\"post\">");
        out.println("<table border=0 cellpadding=2>");
        out.println("<tr><td>Your name:</td><td><input name=\"author\" type=\"text\" size=70></td><td></td></tr>");
        out.println("<tr><td>Your tweet:</td><td><textarea name=\"tweet_text\" rows=\"2\" cols=\"70\" wrap></textarea></td>");
        out.println("<td><input type=\"submit\" name=\"action\" value=\"Tweet!\"></td></tr>");
        out.println("</table></form></div>");
        String currentDate = "None";
        for (Tweet tweet: tweets) {
            String messDate = dateFormatter.format(tweet.getCreated_at());
            if (!currentDate.equals(messDate)) {
                out.println("<br><h3>...... " + messDate + "</h3>");
                currentDate = messDate;
            }
            out.println("<div class=\"wallitem\">");
            out.println("<h4><em>" + tweet.getAuthor() + "</em> @ "+ timeFormatter.format(tweet.getCreated_at()) +"</h4>");
            out.println("<form method=\"post\">");
            out.println("<p>" + tweet.getText() + "&nbsp;&nbsp;&nbsp;<td><input type=\"submit\" name=\"action\" value=\"Esborra\"></td></tr>" + "</p>");
            out.println("<tr><td><input type=\"hidden\" name=\"twid\" value=" + tweet.getTwid() + "></td></tr>");
            out.println("</form>");
            out.println("</div>");
        }
        out.println ( "</body></html>" );
    }
    
    public static String sha256(String base) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(base.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();

	        for (int i = 0; i < hash.length; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if (hex.length() == 1) {
	            	hexString.append('0');
	            }
	            hexString.append(hex);
	        }

	        return hexString.toString();
	    } catch(Exception e) {
	    	throw new RuntimeException(e);
	    }
	}
    
}