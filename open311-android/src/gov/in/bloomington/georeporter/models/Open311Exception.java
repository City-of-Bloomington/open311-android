/**
 * @copyright 2012 City of Bloomington, Indiana
 * @license http://www.gnu.org/licenses/gpl.txt GNU/GPL, see LICENSE.txt
 * @author Cliff Ingham <inghamn@bloomington.in.gov>
 */
package gov.in.bloomington.georeporter.models;

public class Open311Exception extends Exception {
    private static final long serialVersionUID = -1630676957633889696L;
    private String dialogMessage;
    
    public Open311Exception(String message) {
        dialogMessage = message;
    }
    
    public String getDialogMessage() {
        return dialogMessage;
    }
}
