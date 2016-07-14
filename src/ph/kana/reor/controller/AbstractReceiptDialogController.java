package ph.kana.reor.controller;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.converter.BigDecimalStringConverter;
import ph.kana.reor.util.DialogsUtil;

public abstract class AbstractReceiptDialogController extends AbstractWindowController implements Initializable {

	@FXML protected TextField titleTextField;
	@FXML protected TextField amountTextField;
	@FXML protected DatePicker receiptDatePicker;
	@FXML protected ListView<File> attachmentList;
	@FXML protected TextArea desciptionTextArea;
	@FXML protected CheckBox warrantyCheckbox;
	@FXML protected CheckBox lifetimeWarrantyCheckbox;
	@FXML protected DatePicker warrantyDatePicker;
	@FXML protected TextField tagsTextField;

	@FXML protected AnchorPane rootPane;
	@FXML protected HBox warrantyBox;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		amountTextField.setTextFormatter(getBigDecimalTextFormatter());
	}

	@FXML
	public void addFileButtonClick() {
		List<File> attachments = DialogsUtil.showAttachmentsFileChooser(getWindow());
		addAttachments(attachments);
	}

	@FXML
	public void warrantyToggleClick() {
		boolean hasWarranty = warrantyCheckbox.isSelected();
		warrantyBox.setDisable(!hasWarranty);
	}

	@FXML
	public void lifetimeWarrantyToggleClick() {
		boolean lifetimeWarranty = lifetimeWarrantyCheckbox.isSelected();
		warrantyDatePicker.setDisable(lifetimeWarranty);
		if (lifetimeWarranty) {
			warrantyDatePicker.setValue(null);
		}
	}

	@FXML
	public void cancelButtonClick() {
		getWindow().close();
	}

	private TextFormatter<BigDecimal> getBigDecimalTextFormatter() {
		return new TextFormatter(new BigDecimalStringConverter(), BigDecimal.ZERO);
	}

	private void addAttachments(List<File> attachments) {
		List<File> existingAttachments = attachmentList.getItems();
		attachments.forEach((File attachment) -> {
			if (!existingAttachments.contains(attachment)) {
				existingAttachments.add(attachment);
			}
		});
	}
}