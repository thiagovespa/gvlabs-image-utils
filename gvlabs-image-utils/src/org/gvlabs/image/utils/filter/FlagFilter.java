package org.gvlabs.image.utils.filter;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Versão inicial do filtro de bandeira. Licenciado sob GPL:
 * http://www.gnu.org/licenses/gpl.html
 * 
 * @author Thiago Galbiatti Vespa - <a
 *         href="mailto:thiago@thiagovespa.com.br">thiago@thiagovespa.com.br</a>
 * @version 0.6
 * 
 */
public class FlagFilter implements ImageFilter {

	private boolean x;
	private boolean y;

	/**
	 * Construtor padrão. O efeito é aplicado no eixo x e y
	 */
	public FlagFilter() {
		this.x = true;
		this.y = true;
	}

	/**
	 * Construtor que especifica onde aplicar o filtro
	 * 
	 * @param x
	 *            se verdadeiro aplica no eixo x
	 * @param y
	 *            se verdadeiro aplica no eixo y
	 */
	public FlagFilter(boolean x, boolean y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public BufferedImage applyTo(BufferedImage src) {

		int w = src.getWidth();
		int h = src.getHeight();

		// TODO: Remover uso de matrizes desnecessárias
		int[][] resultMatrix = new int[w][h];

		// TODO: Otimizar. Talvez utilizar PixelGrabber
		
		// Copia para matriz
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int value = src.getRGB(i, j);
				resultMatrix[i][j] = value;
			}
		}

		int[] transformY = new int[w];

		if (this.y) {

			// Cosseno horizontal
			for (int i = 0; i < w; i++) {
				// TODO: Colocar variáveis como parâmetro
				double x = (i / (double) (w - 1)) * 3 * Math.PI;
				double valor = 0.27 * (((Math.cos(x) + 1) * ((double) (h - 1) / 2.0)));
				transformY[i] = (int) valor;
			}
		}
		MinMaxReturn increment = getNormValue(transformY);

		int[] transformX = new int[h + increment.getDelta()];

		int[][] transformMatrix = new int[(w)][h + increment.getDelta()];

		for (int i = 0; i < w; i++) {
			Arrays.fill(transformMatrix[i], 0xffffff);
			for (int j = 0; j < h; j++) {
				int desloc = (j - transformY[i]);
				transformMatrix[i][increment.getSum() + desloc] = resultMatrix[i][j];
			}
		}
		if (this.x) {

			// Cosseno vertical
			for (int i = 0; i < h + increment.getDelta(); i++) {
				// TODO: Colocar variáveis como parâmetro
				double y = (i / (double) (h - 1)) * 1.5 * Math.PI + 16;
				double valor = (((2 * Math.cos(y) + 1) * ((double) (w - 1) / 2.0)) * 0.07) + 7;
				transformX[i] = (int) valor;
			}
		}
		MinMaxReturn incrementX = getNormValue(transformX);

		int[][] transform2Matrix = new int[(w + incrementX.getDelta())][h
				+ increment.getDelta()];

		for (int i = 0; i < transform2Matrix.length; i++) {
			Arrays.fill(transform2Matrix[i], 0xffffff);
		}

		for (int j = h + increment.getDelta() - 1; j >= 0; j--) {

			for (int i = w - 1; i >= 0; i--) {
				int desloc = (i - transformX[j]);
				transform2Matrix[incrementX.max + desloc][j] = transformMatrix[i][j];
			}
		}

		int[] resultImage = new int[(w + incrementX.getDelta())
				* (h + increment.getDelta())];

		// Matrix de conversão
		for (int i = 0; i < transform2Matrix.length; i++) {
			for (int j = 0; j < transform2Matrix[0].length; j++) {
				resultImage[j * (transform2Matrix.length) + i] = transform2Matrix[i][j];
			}
		}
		BufferedImage dest = new BufferedImage(transform2Matrix.length,
				transform2Matrix[0].length, BufferedImage.TYPE_INT_RGB);

		dest.setRGB(0, 0, transform2Matrix.length, transform2Matrix[0].length,
				resultImage, 0, transform2Matrix.length);

		return dest;
	}

	class MinMaxReturn {
		int min;
		int max;

		int getDelta() {
			return max - min;
		}

		int getSum() {
			return max + min;
		}
	}

	// TODO: Colocar esse método em classe utilitária
	private MinMaxReturn getNormValue(int[] numbers) {
		MinMaxReturn ret = new MinMaxReturn();
		ret.max = numbers[0];
		ret.min = numbers[0];
		for (int i = 1; i < numbers.length; i++) {
			if (numbers[i] > ret.max) {
				ret.max = numbers[i];
			}
			if (numbers[i] < ret.min) {
				ret.min = numbers[i];
			}
		}

		return ret;
	}

}
