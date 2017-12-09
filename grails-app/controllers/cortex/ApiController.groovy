package cortex
import groovy.json.*;

class ApiController {

    def index() {
		getItemData()
    }

	void getItemData() {
		def jsonSlurper = new JsonSlurper()
		def reader = new BufferedReader(new InputStreamReader(new FileInputStream("/tmp/resultado-1-turno.json"),"UTF-8"))
		def data = jsonSlurper.parse(reader).toArray();
		
		def arrayRegiao		 = new ArrayList();

		///Montando um JSON chave:valor
		data.each{ ///No da regiao
			x->
				boolean ehEstado = false;
				boolean ehPais   = false;
				
				println '##########################################################'
				println '##########################################################'
				println '##########################################################'
				println '##########################################################'
				if(x[0] == "UF") {
					ehEstado = true
				} else if(x[0] == "BR" || x[0] == "#N/A"){
					ehPais = true;
				}

				///Verificando se e um pais, e bypassando
				if(ehPais == false) {

					def arrayCandidato  = new ArrayList();
					def hashRegiao 		= new HashMap();
					
					if(ehEstado == false) {
						String municipio 	= x[0];
						String codigo  		= x[1];
						String quantidade 	= x[3];
						String estado 		= x[4];
						String uf 			= x[5];
						

						hashRegiao.municipio = municipio
						hashRegiao.codigo = codigo
						hashRegiao.quantidade = quantidade
						hashRegiao.estado = estado
						hashRegiao.uf = uf
					} else {
						String uf 	  = x[1];
						String estado = x[2];

						hashRegiao.estado = estado
						if(!estado.contains("exterior"))
							hashRegiao.capital = 'CAPITAL'
						hashRegiao.uf = uf
					}

					x.each{ ///No do candidato
						y->						
							def hashCandidato = new HashMap();
							
							if(y instanceof List) { ///Verificando se e um array
								hashCandidato.partido = y[0];
								hashCandidato.nome = y[1];
								hashCandidato.votos = y[2];
								hashCandidato.porcentVotos = y[3];
								
								arrayCandidato.add(hashCandidato);
								hashRegiao.candidato = arrayCandidato;
							}
					}
					arrayRegiao.add(hashRegiao);
					println  new groovy.json.JsonBuilder(hashRegiao).toString();
				}
		}

		arrayRegiao.each{
			regiao->
			 	println 'regiao ->>> '+regiao.municipio
			 	if(!regiao.capital) {
			 		///Criando municipio
			 		if(!Municipio.findByCodigo(Long.parseLong(regiao.codigo))) {
			 			
			 			println 'municipio -> '+regiao.municipio+ ' nao existe, criando um novo..'
						
						def municipio = new Municipio();

						municipio.codigo 	= Long.parseLong( regiao.codigo);
						municipio.nome 		= regiao.municipio;
						municipio.uf 		= regiao.uf;
						municipio.eleitores = Long.parseLong(regiao.quantidade);
						
						if(!municipio.save(flush:true)){
							municipio.errors.each{
								println 'error ao cadastrar novo municipio -> '+it;
							}
						}
			 		}
				} else {

			 		///Criando estado
			 		if(!Estado.findByNome(regiao.estado)) {
			 			
			 			println 'estado -> '+regiao.estado+ ' nao existe, criando um novo..'
						
						def estado 	= new Estado();

						estado.nome = regiao.estado;
						estado.uf 	= regiao.uf;
						
						if(!estado.save(flush:true)){
							estado.errors.each{
								println 'error ao cadastrar novo estado -> '+it;
							}
						}
			 		}
			 	}

				regiao.candidato.each{
				 	candidato->

				 		///Criando partido
				 		if(!Partido.findBySigla(candidato.partido)) {
				 			
				 			println 'partido -> '+candidato.partido+ ' nao existe, criando um novo..'
							
							def partido = new Partido();
							partido.sigla = candidato.partido;
							
							if(!partido.save(flush:true)){
								partido.errors.each{
									println 'error ao cadastrar novo partido -> '+it;
								}
							}
				 		}			 		


				 		///Criando candidato e relacionando com partido
				 		if(!Candidato.findByNome(candidato.nome)) {
				 			
				 			println 'candidato -> '+candidato.nome+ ' nao existe, criando um novo..'
							
							def novoCandidato = new Candidato();
							def partido 	  = Partido.findBySigla(candidato.partido);
							
							novoCandidato.nome = candidato.nome;
							novoCandidato.partido = partido;
							
							if(!novoCandidato.save(flush:true)){
								novoCandidato.errors.each{
									println 'error ao cadastrar novo candidato -> '+it;
								}
							}
				 		}
				}
		}
		///println  new groovy.json.JsonBuilder(arrayRegiao).toString();

	}
}