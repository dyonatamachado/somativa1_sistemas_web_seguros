package sisrh.rest;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;

import io.swagger.annotations.Api;
import sisrh.banco.Banco;
import sisrh.dto.Empregado;
import sisrh.dto.Solicitacao;

@Api
@Path("/solicitacoes")
public class SolicitacoesRest {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response listarSolicitacoes() throws Exception {
		List<Solicitacao> lista = Banco.listarSolicitacoes();		
		GenericEntity<List<Solicitacao>> entity = new GenericEntity<List<Solicitacao>>(lista) {};
		return Response.ok().entity(entity).build();
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response obterSolicitacao(@PathParam("id") Integer id) throws Exception {
		try {
			Solicitacao solicitacao = Banco.buscarSolicitacaoPorId(id);
			if ( solicitacao != null ) {
				return Response.ok().entity(solicitacao).build();
			}else {
				return Response.status(Status.NOT_FOUND)
						.entity("{ \"mensagem\" : \"Solicitação nao encontrada!\" }").build();
			}
		}catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("{ \"mensagem\" : \"Falha para obter solicitação!\" , \"detalhe\" :  \""+ e.getMessage() +"\"  }").build();
		}
	}
	
	@POST	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response incluirSolicitacao(Solicitacao solicitacao) {
		try 
		{
			String matricula = solicitacao.getMatricula();
			if(matricula == null) Response.status(Status.BAD_REQUEST)
						.entity("{ \"mensagem\" : \"Informe a matrícula!\" }").build();
			
			Empregado empregado = Banco.buscarEmpregadoPorMatricula(matricula);
			if(empregado == null || empregado.getDesligamento() != null) 
			{
				return Response.status(Status.BAD_REQUEST)
						.entity("{ \"mensagem\" : \"Matrícula informada não é válida e/ou não pertence a nenhum Empregado ativo\" }").build();
			}
			
			Solicitacao sol = Banco.incluirSolicitacao(solicitacao);
			return Response.ok().entity(sol).build();
		}
		catch (Exception e) 
		{
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("{ \"mensagem\" : \"Falha na inclusao da solicitação!\" , \"detalhe\" :  \""+ e.getMessage() +"\"  }").build();
		}		
	}
	
	@PUT	
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response alterarSolicitacao(@PathParam("id") Integer id, Solicitacao solicitacao)  {
		try {			
			String matricula = solicitacao.getMatricula();
			if(matricula == null) Response.status(Status.BAD_REQUEST)
						.entity("{ \"mensagem\" : \"Informe a matrícula!\" }").build();
			
			Empregado empregado = Banco.buscarEmpregadoPorMatricula(matricula);
			if(empregado == null || empregado.getDesligamento() != null) 
			{
				return Response.status(Status.BAD_REQUEST)
						.entity("{ \"mensagem\" : \"Matrícula informada não é válida e/ou não pertence a nenhum Empregado ativo\" }").build();
			}
			
			if ( Banco.buscarSolicitacaoPorId(id) == null ) {				
				return Response.status(Status.NOT_FOUND)
						.entity("{ \"mensagem\" : \"Solicitação não encontrada!\" }").build();
			}
			
			Banco.alterarSolicitacao(id, solicitacao);	
			return Response.ok().entity(solicitacao).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity("{ \"mensagem\" : \"Falha na alteração da solicitação!\" , \"detalhe\" :  \""+ e.getMessage() +"\"  }").build();
		}
	}
	
	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response excluirSolicitação(@PathParam("id") Integer id) throws Exception {
		try {
			if ( Banco.buscarSolicitacaoPorId(id) == null ) {				
				return Response.status(Status.NOT_FOUND).
						entity("{ \"mensagem\" : \"Solicitação não encontrada!\" }").build();
			}				
			Banco.excluirSolicitacao(id);
			return Response.ok().entity("{ \"mensagem\" : \"Solicitação "+ id + " excluída!\" }").build();	
		}catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).
					entity("{ \"mensagem\" : \"Falha na exclusão da solicitação!\" , \"detalhe\" :  \""+ e.getMessage() +"\"  }").build();
		}		
	}
}
