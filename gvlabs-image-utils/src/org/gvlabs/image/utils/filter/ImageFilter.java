package org.gvlabs.image.utils.filter;

import java.awt.image.BufferedImage;

/**
 * Interface à ser utilizada em todos os filtros de imagem
 * 
 * @author Thiago Galbiatti Vespa - <a
 *         href="mailto:thiago@thiagovespa.com.br">thiago@thiagovespa.com.br</a>
 * @version 1.0
 *
 */
public interface ImageFilter {
	/**
	 * Aplica o filtro à imagem
	 * @param src Imagem original
	 * @return Imagem com o filtro aplicado
	 */
	public BufferedImage applyTo(BufferedImage src);
}
