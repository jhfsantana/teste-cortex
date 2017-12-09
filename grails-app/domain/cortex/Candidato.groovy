package cortex

class Candidato {

	String nome;
	
	static belongsTo = [partido: Partido]
    
    static constraints = {
    }
}
