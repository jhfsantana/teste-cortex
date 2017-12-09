package cortex

class CandidatoMunicipio {

	Long votos;
	static belongsTo = [candidato: Candidato, municipio: Municipio]
    static constraints = {
    }
}
