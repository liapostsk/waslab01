package fib.asw.waslab01_cs;

import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;

//This code uses the Fluent API

public class SimpleFluentClient {

	private static String URI = "http://localhost:8080/waslab01_ss/";

	public final static void main(String[] args) throws Exception {
  	
  	/* Insert code for Task #4 here */
		//Creem un tweet per comprovar com s'esborra
		String id =
	Request.post(URI).bodyForm(Form.form().add("author",  "Lia&Enrique").add("tweet_text",  "Aquest tweet serà eliminat").build())
	.addHeader("Accept", "text/plain").execute().returnContent().asString();
		id = id.substring(0, id.length()-1);
		
		//Imprimim per mostrar el tweet creat
		System.out.println(Request.get(URI).addHeader("Accept", "text/plain").execute().returnContent());

		
	

  	/* Insert code for Task #5 here */
		
		//Esborrem el tweet creat
		Request.post(URI).bodyForm(Form.form().add("twid", id).build()).addHeader("Accept", "text/plain").execute();
		
		//Imprimim per mostrar el tweet esborrat
	  	System.out.println(Request.get(URI).addHeader("Accept", "text/plain").execute().returnContent());


	  	
  	
  	
  }
}