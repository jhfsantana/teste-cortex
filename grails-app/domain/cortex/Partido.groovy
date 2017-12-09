package cortex

class Partido {

	String sigla;

	static hasMany = [candidatos: Candidato];

    static constraints = {
    }
}
