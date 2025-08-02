package br.com.ymf.amplis.base.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import br.com.ymf.amplis.base.exception.ProgrammingError;

import com.google.common.collect.Lists;

/**
 * Classe utilitária que auxilia na execução de queries com a claúsula IN com mais de 1000 registros.
 * 
 * @author f.cabral
 */
public final class HQLHelper {
	private static final String	NAMED_PARAMETER	= "_np_";

	private static final Logger	logger			= Logger
														.getLogger(HQLHelper.class
																.getName());

	private static final int	MAX_IN_ELEMENTS	= 1000;

	/**
	 * Fornece uma query com o named-parameter da cláusula "in" devidamente tratado e atribuido, evitando problemas de
	 * execução de query com cláusulas in com mais de 1000 elementos. Para que seja possível executar a query deve-se
	 * passar uma sessão do hibernate. É de responsabilidade do cliente informar o contexto de sessão corrento, com
	 * chinese-wall ou migration habilitado-desabilitado, sessão temporário ou a current-session da transação. Se algum
	 * dos parâmetros for null um ProgrammingError será lançado.
	 * 
	 * @param session
	 *            session do hibernate.
	 * @param values
	 *            lista de valores
	 * @param hqlOrNamedQuery
	 *            hql ou named-query
	 * @param namedParameter
	 *            named-parameter da cláusula in.
	 * @return Nova query com o parâmetro in devidamente tratado e atribuido
	 * @throws ProgrammingError
	 *             se algum dos parâmetros não forem corretamente informados.
	 */
	public static Query replaceClauseIn(final Session session,
			final String hqlOrNamedQuery, final String namedParameter,
			final Collection<?> values) {
		final Map<String, Collection<?>> map = new HashMap<String, Collection<?>>();
		map.put(namedParameter, values);

		return replaceClauseIn(session, hqlOrNamedQuery, map);
	}

	/**
	 * Fornece uma query com o named-parameter da cláusula "in" devidamente tratado e atribuido, evitando problemas de
	 * execução de query com cláusulas in com mais de 1000 elementos. Para que seja possível executar a query deve-se
	 * passar uma sessão do hibernate. É de responsabilidade do cliente informar o contexto de sessão corrento, com
	 * chinese-wall ou migration habilitado-desabilitado, sessão temporário ou a current-session da transação. Esse
	 * método deve ser utilizado para resolver problemas de cláusula in com mais de 1000 parâmetros quando há mais de
	 * uma cláusula in. Os parâmetros que não foram coleções maiores que 1000 serão passados sem alteração para a
	 * consulta
	 * 
	 * @param session
	 *            session do hibernate.
	 * @param hqlOrNamedQuery
	 *            named-query ou hql
	 * @param map
	 *            mapa de nome do parâmetro -> parâmetro
	 * @return Nova query com o parâmetro in devidamente tratado e atribuido
	 * @throws ProgrammingError
	 *             se algum dos parâmetros não forem corretamente informados.
	 */
	public static Query replaceClauseIn(Session session,
			String hqlOrNamedQuery, Map<String, ? extends Object> valuesByParams) {

		Map<String, Collection<?>> collectionParams = new HashMap<String, Collection<?>>();
		Map<String, Object> nonCollectionParams = new HashMap<String, Object>();

		for (String params : valuesByParams.keySet()) {
			if (valuesByParams.get(params) instanceof Collection<?>) {
				collectionParams.put(params, (Collection<?>) valuesByParams
						.get(params));
			}
			else {
				nonCollectionParams.put(params, valuesByParams.get(params));
			}
		}
		Query query = getQuery(session, hqlOrNamedQuery, collectionParams);

		for (String params : nonCollectionParams.keySet()) {
			query.setParameter(params, nonCollectionParams.get(params));
		}

		return query;
	}

	/**
	 * Obtém a query hibernate para execução com os parâmetros da cláusula in devidamente atribuidos.
	 * 
	 * @param session
	 *            sessão hibernate
	 * @param hqlOrNamedQuery
	 *            named-query ou hql
	 * @param map
	 *            mapa de valores da cláusula in (key=named-parameter, value=collection)
	 * @return query com os parâmetros atribuidos
	 * @throws ProgrammingError
	 */
	static Query getQuery(final Session session, final String hqlOrNamedQuery,
			final Map<String, Collection<?>> map){
		final Map<String, Collection<?>> filtered = filterMap(map);

		Query query = null;

		if (!map.isEmpty()) {
			// Se o mapa não estiver vazio significa que há cláusulas in que devem ser substituidas
			query = translateIn(session, hqlOrNamedQuery, map);
		}
		else {
			/*
			 * Se o mapa estiver vazio significa que todos os valores são <= 1000 e as cláusulas não precisam ser
			 * substituidas
			 */
			query = getHibernateQuery(session, hqlOrNamedQuery);
		}

		if (!filtered.isEmpty()) {
			/*
			 * Se o mapa filtrado não estiver vazio então há valores <= 1000. Esses named-parameters não foram
			 * substituidos e devem ser atribuidos.
			 */
			for (final Map.Entry<String, Collection<?>> entry : filtered
					.entrySet()) {
				final String param = entry.getKey();
				final Collection<?> values = entry.getValue();

				query.setParameterList(param, values);
			}
		}

		return query;
	}

	/**
	 * Filtra o mapa original removendo do mapa todos as chaves cujo valor é menor ou igual a 1000 (<=1000). Isso para
	 * que o named-parameter não seja substituido por {@link #translateIn(Session, String, Map)}. Um mapa contendo
	 * somente chaves cujo valor é menor ou igual a 1000 será obtido. O mapa passado como parâmetro será filtrado.
	 * 
	 * @param map
	 *            mapa contendo os valores
	 * @return mapa apenas com valores menores ou igual a 1000.
	 */
	static Map<String, Collection<?>> filterMap(
			final Map<String, Collection<?>> map) {
		final Map<String, Collection<?>> copy = new HashMap<String, Collection<?>>(
				map);
		final Map<String, Collection<?>> filtered = new HashMap<String, Collection<?>>();

		for (final Map.Entry<String, Collection<?>> entry : copy.entrySet()) {
			final String param = entry.getKey();
			final Collection<?> values = entry.getValue();

			if (values.size() <= MAX_IN_ELEMENTS) {
				filtered.put(param, values);
				map.remove(param);
			}
		}

		return filtered;
	}

	/**
	 * * Traduz uma query com parâmetro parâmetro in e vários ins submetidos a um or. <br>
	 * <br>
	 * Exemplo:<br>
	 * <code> select * from Teste entity where entity.id in (dois_mil_registros)</code> <br>
	 * <br>
	 * O método retornará:<br>
	 * <code>select * from Teste entity where (entity.id in (mil_registros) or entity.id in (mil_registros))</code>
	 * 
	 * @param session
	 *            session do hibernate
	 * @param values
	 *            lista de valores para cláusula in
	 * @param hqlOrNamedQuery
	 *            named-query ou hql que contém a cláusula in
	 * @param param
	 *            named-parameter da cláusula in
	 * @return nova query com cláusula in substituida e com os valores atribuidos
	 */
	private static Query translateIn(final Session session,
			final String hqlOrNamedQuery, final Map<String, Collection<?>> map) {
		final Query query = getHibernateQuery(session, hqlOrNamedQuery);

		final String newQueryString = replaceQueryString(
				query.getQueryString(), map);

		logger.trace("Query original: " + query.getQueryString());
		logger.trace("Query gerada: " + newQueryString);

		// Cria a nova query
		final Query newQuery = session.createQuery(newQueryString);

		for (final Map.Entry<String, Collection<?>> entries : map.entrySet()) {
			final String param = entries.getKey();
			final Collection<?> values = entries.getValue();

			setParameterList(param, newQuery, values);
		}
		return newQuery;
	}

	/*
	 * Obtém uma query hibernate a partir de uma named-query ou hql
	 */
	private static Query getHibernateQuery(final Session session,
			final String hqlOrNamedQuery) {
		// Caso nao seja uma namedQuery assume-se que sera um hql
		try {
			return session.getNamedQuery(hqlOrNamedQuery);
		} catch (final HibernateException e) {
			logger
					.trace(
							"NamedQuery não encontrada. Assumindo parâmetro como um hql.",
							e);

			return session.createQuery(hqlOrNamedQuery);
		}
	}

	/**
	 * Substitui a string da query por uma equivalente de tal forma que seja possível executar a query com cláusula in
	 * com mais de 1000 parâmetros.
	 * 
	 * @param queryString
	 *            string da query
	 * @param map
	 *            mapa de valores da cláusula in
	 * @return nova query
	 */
	private static String replaceQueryString(final String queryString,
			final Map<String, Collection<?>> map) {
		String query = queryString;

		for (final Map.Entry<String, Collection<?>> entries : map.entrySet()) {
			// faz replace do in pelos novos "in" gerados
			query = replaceQueryString(query, entries.getKey(), entries
					.getValue().size());
		}

		return query;
	}

	/**
	 * Faz atribuição dos valores nas cláusulas in que foram geradas. Esquema de como é feita a atribuição dos
	 * named-parameters das cláusulas in:<br>
	 * <br>
	 * <br>
	 * Supondo uma coleção de 3500 elementos, será gerado 4 novos ins suportando 1000, 1000, 1000 e 500 elementos cada
	 * in. <br>
	 * <br>
	 * Cláusula in original => <code>model.property in (1,2,3,4, ... , 3500)</code> <br>
	 * <br>
	 * Cláusula in gerada =><code>(model.property in(1,2,3,...,1000) or 
	 * 							   model.property in(1001,1002,1003,...,2000) or 
	 * 							   model.property in(2001,2002,2003,...,3000) or 
	 * 							   model.property in(3001,3002,3003,...,3500))</code> <br>
	 * Em caso de not in => <code>not (model.property in(1,2,3,...,1000) or 
	 * 							 	   model.property in(1001,1002,1003,...,2000) or 
	 * 							   	   model.property in(2001,2002,2003,...,3000) or 
	 * 							   	   model.property in(3001,3002,3003,...,3500))</code> <br>
	 * 
	 * @param param
	 *            nome do named-parameter da query original
	 * @param newQuery
	 *            nova query gerada
	 * @param values
	 *            coleção de valores para cláusula in
	 */
	private static <T> void setParameterList(final String param,
			final Query newQuery, final Collection<T> values) {
		// Cria uma lista contendo uma lista de size 1000 partir de uma collection
		final List<List<T>> partitionedList = Lists.partition(Lists.newArrayList(values),
				MAX_IN_ELEMENTS);
		int count = 0;

		for (final List list : partitionedList) {
			final StringBuilder newNameParam = new StringBuilder();
			newNameParam.append(param);
			newNameParam.append(count++);
			newNameParam.append(NAMED_PARAMETER);

			// Obtém os novos named-parametres. faz o set do novo named-parameter com os valores da lista
			newQuery.setParameterList(newNameParam.toString(), list);
		}
	}

	/*
	 * Calcula a quantidade de cláusulas in devem ser geradas para satisfazer todos os elementos da coleção
	 */
	private static int calculateNumberOfIn(final int collectionSize) {
		// resto coleção.size pelo que in suporta. Útil para saber quantos itens serão inseridos na última cláusula in
		final int mod = (collectionSize % MAX_IN_ELEMENTS);
		// cálcula quantos operadores in devem ser injetados na query.
		return (collectionSize / MAX_IN_ELEMENTS) + (mod == 0 ? 0 : 1);
	}

	/**
	 * Faz replace da cláusula in da query original por vários ins de acordo com o tamanho da coleção. Retorna uma nova
	 * query com a cláusula in substituida. Esse método tem o modificador de acesso propositalmente definido como
	 * default para que seja possível executar cenários de teste unitário sobre essa rotina.
	 * 
	 * @param param
	 *            nome do named-parameter da cláusula in.
	 * @param queryString
	 *            query original como uma string, ex: Query.getQueryString()
	 * @param collectionSize
	 *            tamanho da coleção de valores da cláusula in
	 * @return nova query como uma string
	 */
	static String replaceQueryString(final String queryString,
			final String param, final int collectionSize) {
		final int numberOfIn = calculateNumberOfIn(collectionSize);

		logger.trace("Substituindo cláusula in (:" + param + ") com "
				+ collectionSize + " parâmetros por " + numberOfIn
				+ " cláusulas in.");

		/**
		 * Expressão regular que procura pelo padrão ou ocorrência de cláusula in em uma query:<br>
		 * 1) ... classe.qualquer.propriedade in (:named-parameter) ... <br>
		 * 2) ... classe.qualquer.propriedade not in (:named-parameter) ... <br>
		 * onde: <br>
		 * "..." significa qualquer coisa: select, from, where, uma classe, outras cláusulas, etc.
		 */
		final String regex = "([\\w\\.]+)\\s+(not\\s+)?in\\s*\\(\\s*:" + param
				+ "\\s*\\)";

		final StringBuilder in = new StringBuilder(" $2 (");

		// cria as novas cláusulas in que substituirá a cláusula in original
		for (int i = 0; i < numberOfIn; i++) {
			// Cria um novo named-parameter.
			final StringBuilder newNameParam = new StringBuilder();
			newNameParam.append(param);
			newNameParam.append(i);
			newNameParam.append(NAMED_PARAMETER);

			/*
			 * gera o string do replacement pelo agrupamento definido no pattern: $1 é a propriedade que está sendo
			 * feito in.
			 */
			in.append("$1 in (:");
			in.append(newNameParam.toString());
			in.append(")");

			if (i + 1 < numberOfIn) {
				// Se houver mais uma iteração do loop então faz a inclusão do operador lógico or entre as cláusulas in
				in.append(" or ");
			}
		}

		in.append(") ");// fecha todos os in

		// faz replace do in pelos novos "in" gerados
		return queryString.replaceAll(regex, in.toString());

	}

	private HQLHelper() {
		// não deve haver instâncias dessa classe.
	}

	/**
	 * Cria uma uma lista contendo sublistas com no máximo 1000 registros.
	 * @param lista lista de ids
	 * @return uma lista contendo listas de no máximo 1000 ids
	 */
	public static <T> List<List<T>> criaSubListasDe1000Elementos(Collection<T> lista){
		List<T> copia = new ArrayList<T>(lista);
		int qtdeDeIn=calculateNumberOfIn(copia.size());
		List<List<T>> resultado = new ArrayList<List<T>>();
		
		for(int i=0, fromIndex=0; i < qtdeDeIn; i++){
			if(i + 1 < qtdeDeIn){
				int toIndex = fromIndex + MAX_IN_ELEMENTS ;
				//adiciona de 1000 em 1000
				resultado.add(copia.subList(fromIndex, toIndex));
				fromIndex = toIndex;
			} else {
				//na última iteração adiciona o que faltou
				resultado.add(copia.subList(fromIndex, copia.size()));
			}
		}
		
		return resultado;
	}
}
