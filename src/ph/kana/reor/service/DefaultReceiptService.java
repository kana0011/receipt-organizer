package ph.kana.reor.service;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import ph.kana.reor.dao.ReceiptDao;
import ph.kana.reor.dao.derby.DerbyReceiptDao;
import ph.kana.reor.exception.DataAccessException;
import ph.kana.reor.exception.ServiceException;
import ph.kana.reor.model.Attachment;
import ph.kana.reor.model.Document;
import ph.kana.reor.model.Receipt;
import ph.kana.reor.model.Warranty;

public class DefaultReceiptService implements ReceiptService {

	private final ReceiptDao receiptDao = new DerbyReceiptDao();
	private final CategoryService categoryService = new DefaultCategoryService();

	@Override
	public Receipt createReceipt(String title, BigDecimal amount, LocalDate receiptDate, Set<File> attachments, String description, Warranty warranty, String category) throws ServiceException {
		try {
			Receipt receipt = new Receipt();
			receipt.setTitle(title);
			receipt.setAmount(amount);
			receipt.setDate(receiptDate);
			receipt.setAttachments(transformFilesToAttachments(receipt, attachments));
			receipt.setDescription(description);
			receipt.setWarranty(warranty);
			receipt.setCategory(categoryService.fetchCategory(category));

			return receiptDao.save(receipt);
		} catch (DataAccessException e) {
			throw new ServiceException(e);
		}
	}

	private Set<Attachment> transformFilesToAttachments(Document document, Set<File> files) {
		Set<Attachment> attachments = new HashSet();
		files.stream()
			.map((file) -> {
				Attachment attachment = new Attachment();
				attachment.setDocument(document);
				attachment.setPath(file.getPath()); // TODO change to actual upload
				return attachment;
			})
			.forEach(attachments::add);

		return attachments;
	}
}
