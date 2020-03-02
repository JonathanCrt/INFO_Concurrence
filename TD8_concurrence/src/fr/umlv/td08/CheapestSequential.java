package fr.umlv.td08;

import java.util.Comparator;
import java.util.Optional;

public class CheapestSequential {

    private final String item;
    private final int timeoutMilliPerRequest;

    public CheapestSequential(String item, int timeoutMilliPerRequest) {
        this.item = item;
        this.timeoutMilliPerRequest = timeoutMilliPerRequest;
    }
    
    
    private Answer sendRequest(Request req) {
    	try {
			var res = req.request(timeoutMilliPerRequest);
			return res;
		} catch (InterruptedException e) {
			throw new AssertionError();
		}
    }
    
    /**
     * @return the cheapest price for item if it is sold
     */
    public Optional<Answer> retrieve() throws InterruptedException {
    	return Request.ALL_SITES
    			.stream()
    			.map((eachSite) -> new Request(eachSite, this.item))
    			.map((request) -> sendRequest(request))
    			.filter((filtre) -> filtre.isSuccessful())
    			.min(Comparator.comparing((answer) -> answer.getPrice()));
    }

    public static void main(String[] args) throws InterruptedException {
        var agregator = new CheapestSequential("pikachu", 2_000);
        var answer = agregator.retrieve();
        System.out.println(answer); // Optional[pikachu@darty.fr : 214]
    }
}
