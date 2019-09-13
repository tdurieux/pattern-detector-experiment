package sic.util;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JTable;

public class Utilidades {

    /**
     * Verifica si ya existe una instancia de la clase @tipo dentro de @desktop
     * 
     * @param desktop Contenedor de internal frames donde debe buscar
     * @param tipo Clase buscada
     * @return Internal frame encontrado, en caso contrario devuelve NULL
     */
    public static JInternalFrame estaEnDesktop(JDesktopPane desktop, Class tipo) {
        JInternalFrame[] frames = desktop.getAllFrames();
        for (JInternalFrame fr : frames) {
            if (tipo.isAssignableFrom(fr.getClass())) {
                return fr;
            }
        }
        return null;
    }

    /**
     * Verifica si existen frames dentro de @desktop
     * 
     * @param desktop Contenenedor de frames donde debe buscar
     * @return True si existen frames dentro del
     * @desktop, false en caso contrario
     */
    public static boolean contieneVentanas(JDesktopPane desktop) {
        JInternalFrame[] frames = desktop.getAllFrames();
        if (frames.length == 0) {
            return false;
        }

        return true;
    }

    public static void cerrarTodasVentanas(JDesktopPane desktop) {
        JInternalFrame[] frames = desktop.getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            frames[i].dispose();
        }
    }

    /**
     * Convierte un caracter de minusculas a mayusculas
     *
     * @param caracter Caracter para ser convertido
     * @return Devuelve el caracter ya convertido a mayusculas
     */
    public static char convertirAMayusculas(char caracter) {
        if ((caracter >= 'a' && caracter <= 'z') || caracter == 'ñ') {
            return (char) (((int) caracter) - 32);
        } else {
            return caracter;
        }
    }

    /**
     * Busca un archivo especificado en el directorio donde se encuentra el JAR
     *
     * @param archivo Nombre del archivo que se desea buscar
     * @return archivo encontrado
     * @throws java.io.FileNotFoundException
     * @throws java.net.URISyntaxException
     */
    public static File getArchivoDelDirectorioDelJAR(String archivo) throws FileNotFoundException, URISyntaxException {
        File fileBuscado = null;
        CodeSource codeSource = Utilidades.class.getProtectionDomain().getCodeSource();
        File jarFile = new File(codeSource.getLocation().toURI().getPath());
        File jarDir = jarFile.getParentFile();

        if (jarDir != null && jarDir.isDirectory()) {
            File propFile = new File(jarDir, archivo);
            fileBuscado = propFile.getAbsoluteFile();
        }

        return fileBuscado;
    }

    /**
     * Encripta el password con MD5
     *
     * @param password String a ser encriptado.
     * @return String encriptado con MD5.
     */
    public static String encriptarConMD5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException ex) {
        }
        return null;
    }

    /**
     * Devuelve los indices de las filas seleccionadas luego de que el JTable
     * haya sido ordenado. Al utilizar getSelectedRows() despues de un
     * ordenamiento, devuelve mal los indices.
     *
     * @param table JTable donde debe buscar los indices correctos
     * @return indices seleccionados
     */
    public static int[] getSelectedRowsModelIndices(JTable table) {
        if (table == null) {
            throw new NullPointerException("table == null");
        }

        int[] selectedRowIndices = table.getSelectedRows();
        int countSelected = selectedRowIndices.length;

        for (int i = 0; i < countSelected; i++) {
            selectedRowIndices[i] = table.convertRowIndexToModel(selectedRowIndices[i]);
        }

        return selectedRowIndices;
    }

    /**
     * Devuelve el indice de la fila seleccionada luego de que el JTable haya
     * sido ordenado. Al utilizar getSelectedRow() despues de un ordenamiento,
     * devuelve mal el indice.
     *
     * @param table JTable donde debe buscar el indice correcto
     * @return indice seleccionado
     */
    public static int getSelectedRowModelIndice(JTable table) {
        if (table == null) {
            throw new NullPointerException("table == null");
        }

        int selectedRowIndice = table.getSelectedRow();
        selectedRowIndice = table.convertRowIndexToModel(selectedRowIndice);

        return selectedRowIndice;
    }

    /**
     * Convierte el archivo en un array de bytes.
     *
     * @param archivo Archivo a ser convertido.
     * @return Array de byte representando al archivo.
     * @throws java.io.IOException
     */
    public static byte[] convertirFileIntoByteArray(File archivo) throws IOException {
        byte[] bArchivo = new byte[(int) archivo.length()];
        FileInputStream fileInputStream = new FileInputStream(archivo);
        fileInputStream.read(bArchivo);
        fileInputStream.close();
        return bArchivo;
    }

    /**
     * Convierte un array de bytes en una Image.
     *
     * @param bytesArray Array de byte a ser convertido.
     * @return Array de bytes convertido en Image.
     */
    public static Image convertirByteArrayIntoImage(byte[] bytesArray) {
        if (bytesArray == null) {
            return null;
        } else {
            ImageIcon logoImageIcon = new ImageIcon(bytesArray);
            return logoImageIcon.getImage();
        }
    }

    /**
     * Valida el tamanio del archivo, teniendo en cuenta el tamanioValido.
     *
     * @param archivo Archivo a ser validado.
     * @param tamanioValido Tamanio maximo en bytes permitido para el archivo.
     * @return Retorna true en caso de que el tamanio sea válido, false en otro caso.
     * @throws FileNotFoundException En caso de que no se encuentre el archivo.
     */
    public static boolean esTamanioValido(File archivo, long tamanioValido) throws FileNotFoundException {
        if (archivo == null) {
            throw new FileNotFoundException();
        }

        if (archivo.length() > tamanioValido) {
            return false;
        } else {
            return true;
        }
    }

    public static void controlarEntradaSoloNumerico(KeyEvent evt) {
        char c = evt.getKeyChar();
        if ((c < '0' || c > '9')) {
            evt.consume();
        }
    }
    
}
