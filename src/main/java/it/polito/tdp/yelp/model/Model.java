package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	YelpDao dao;
	Graph<Business, DefaultWeightedEdge> grafo;
	Map<String, Business> idMap;
	
	public Model() {
		dao = new YelpDao();
	}
	
	
	public void creaGrafo(int anno, String citta) {
		grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		idMap = new HashMap<>();
		
		
		//riempio la mappa
		dao.calcolaVertici(anno, citta, idMap);
		
		//Aggiungo i vertici
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		//Aggiungo gli archi
		List<Adiacenza> archi = dao.calcolaArchi(anno, citta, idMap);
		for(Adiacenza a : archi) {
			Graphs.addEdgeWithVertices(this.grafo, a.getB1(), a.getB2(), a.getPeso());
		}
		
	}
	
	
	public int numeroVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int numeroArchi() {
		return this.grafo.edgeSet().size();
	}
	
	
	
	public List<String> getCitta(){
		return dao.getAllCities();
	}
	
//	public String localeMigliore() {
//		double max = Integer.MIN_VALUE;
//		Business best = null;
//		for(Business b: this.grafo.vertexSet()) {
//			double pesoEntrante = 0.0;
//			for(DefaultWeightedEdge e: this.grafo.incomingEdgesOf(b))
//				pesoEntrante += this.grafo.getEdgeWeight(e);
//			
//			double pesoUscente = 0.0;
//			for(DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(b))
//				pesoUscente += this.grafo.getEdgeWeight(e);
//			
//			double peso= pesoEntrante - pesoUscente;
//			
//			if(peso>max) {
//				max=peso;
//				best = b;
//			}
//		}
//		
//		return best.toString();
//	}
//	
	
	
	private List<Business> localiOrdinati(){
		List<Business> locali = new ArrayList<>(idMap.values());
		Collections.sort(locali);
		return locali;
	}
	public Business getLocaleMigliore() {
		double max = 0.0 ;
		Business result = null ;
		
		for(Business b: this.localiOrdinati()) {
			double val = 0.0 ;
			for(DefaultWeightedEdge e: this.grafo.incomingEdgesOf(b))
				val += this.grafo.getEdgeWeight(e) ;
			for(DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(b))
				val -= this.grafo.getEdgeWeight(e) ;
			
			if(val>max) {
				max = val ;
				result = b ;
			}
		}
		return result; 
	}
	
	
	private double getDistanza(Business b1, Business b2) {
		LatLng l = new LatLng(0, 0);
	}
}
