package br.edu.ifpi.opala.performance;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * Classe que constrói o gráfico 
 * @author Tavares
 *
 */
public class Grafico extends ApplicationFrame {

	/**
	 * Atributo necessário para a classe ApplicationFrame
	 */
	private static final long serialVersionUID = 7002697940637147762L;


	/**
	 * Construtor para criação de apenas um gráfico
	 * @param titulo - Texto da parte superior do gráfico.
	 * @param valores - Valores absolutos das ocorrências em ordem de ocorrência
	 * @param textoX - Texto da parte inferior do gráfico.
	 * @param unidade - Valor da unidade de tempo
	 */
	public Grafico(String titulo, ArrayList<Double> valores, String textoX, String unidade) {

		super("Teste de Performance");
		
		ArrayList<Double> medias = new ArrayList<Double>();
		
		double soma = 0;
		int quant = 0;
		for (Double double1 : valores) {
			soma+=double1;			
			medias.add(soma/++quant);
		}

		XYDataset dataset = createDataset(valores, medias);
		JFreeChart chart = createChart(dataset, titulo, textoX, unidade);
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
		setContentPane(chartPanel);
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}

	/**
	 * Constroi os gráficos com os valores informados
	 * @param valores - Valores absolutos das ocorrências em ordem de ocorrência
	 * @param medias - caminho da média de acordo com os valores
	 * @return retorna um objeto que representa os locais que o gráfico deve preencher
	 */
	private XYDataset createDataset(ArrayList<Double> valores, ArrayList<Double> medias ) {

		
		int sequencia = 0;
		
		
		XYSeries series1 = new XYSeries("Tempo por conexão");
		
		XYSeries series2 = new XYSeries("Tempo Medio");
		for (Double media : medias) {
			series2.add(++sequencia, media);
		}
		
		sequencia = 0;
		
		for (Double valor : valores) {
		// series1.add(1.0, 1.0);  (y,  x)
			series1.add(++sequencia, valor);
		} 
		
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		dataset.addSeries(series2);		

		return dataset;

	}

	/**
	 * Constroi toda a janela do gráfico
	 * @param dataset - grafico interno com as retas definidas
	 * @param titulo - Texto da parte superior do gráfico.
	 * @param textoX - Texto da parte inferior do gráfico.
	 * @param unidade - Valor da unidade de tempo
	 * @return Constroi o objeto que representa do gráfico todo preenchido
	 */
	private JFreeChart createChart(XYDataset dataset, String titulo, String textoX, String unidade) {

		// create the chart...
		JFreeChart chart = ChartFactory.createXYLineChart(titulo, // chart
																				// title
				"Média: "+textoX.toString(), // x axis label
				unidade, // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
		);

		// Cor de Fundo
		chart.setBackgroundPaint(Color.white);

		// get a reference to the plot for further customisation...
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.lightGray);// cor interna do grafico
		plot.setDomainGridlinePaint(Color.white);// cor das colunas internas
		plot.setRangeGridlinePaint(Color.white);// cor das linhas internas
		

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(0, true);// numero indica a reta true
												// indica presenca de linha
		renderer.setSeriesShapesVisible(0, false);// numero indica a reta true
													// indica presenca de ponto
		
		
		renderer.setSeriesItemLabelFont(0, new Font(Font.SERIF,Font.ITALIC,10));
		
		
		
		renderer.setSeriesPaint(0, Color.blue);
		renderer.setSeriesPaint(1, Color.red);
		
		plot.setRenderer(renderer);// aplica as alterações

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		return chart;
	}
}