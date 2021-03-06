package ph.kana.reor.controller;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javafx.fxml.FXML;
import ph.kana.reor.controller.common.AbstractReceiptDialogController;
import ph.kana.reor.exception.ServiceException;
import ph.kana.reor.model.Warranty;
import ph.kana.reor.type.MessageType;

public class AddReceiptDialogController extends AbstractReceiptDialogController {

	@Override
	public void initializeForm() {
		super.initializeForm();
		clearForm();
	}

	@FXML
	public void saveButtonClick() {
		save(() -> {
			saveNewReceipt();

			clearForm();
			clearMessages();
			showMessage(formMessageLabel, "Successfully Saved!", MessageType.SUCCESS);
		});
	}

	private void saveNewReceipt() throws ServiceException {
		String title = titleTextField.getText();
		BigDecimal amount = (BigDecimal) amountTextField.getTextFormatter().getValue();
		LocalDate receiptDate = receiptDatePicker.getValue();
		Set<File> attachments = new HashSet(attachmentList.getItems());
		String description = descriptionTextArea.getText();
		Warranty warranty = buildWarranty();
		String category = categoryComboBox.getValue();

		receiptService.createReceipt(title, amount, receiptDate, attachments, description, warranty, category);
	}

	private void clearForm() {
		titleTextField.setText("");
		amountTextField.setText("0");
		receiptDatePicker.setValue(LocalDate.now());
		attachmentList.getItems().clear();
		descriptionTextArea.setText("");
		warrantyCheckbox.setSelected(false);
		warrantyBox.setDisable(true);
		warrantyDatePicker.setValue(null);
		lifetimeWarrantyCheckbox.setSelected(false);
		categoryComboBox.setValue(null);
	}
}
