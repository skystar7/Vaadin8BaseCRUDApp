package com.tigerfixonline.crud.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.tigerfixonline.crud.exception.CRUDException;
import com.tigerfixonline.crud.logging.LoggerUtil;
import com.tigerfixonline.crud.model.Customer;

public class CSVWriter {

	/*
	 * Logger
	 */
	private static Logger logger = LoggerUtil.getLogger(CSVWriter.class);

	private static CellProcessor[] getCellProcessor() {
		final CellProcessor[] cellProcessors = { new NotNull(), new NotNull(), new Optional(), new Optional(),
				new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(),
				new Optional(), new Optional() };

		return cellProcessors;
	}

	public static void collectionWriter(Set<Customer> customers) {
		ICsvBeanWriter beanWriter = null;
		File exported = new File("customers.csv");
		try {
			try {
				beanWriter = new CsvBeanWriter(new FileWriter(exported), CsvPreference.STANDARD_PREFERENCE);
				String[] headers = { "id", "name", "email", "phone", "website", "addressLine1", "addressLine2", "city",
						"state", "zip", "contactPerson", "description" };
				beanWriter.writeHeader(headers);
				for (Customer customer : customers) {
					beanWriter.write(customer, headers, getCellProcessor());
				}

			} finally {
				if (beanWriter != null) {
					beanWriter.close();
				}
			}
		} catch (IOException e) {
			CRUDException crudException = new CRUDException(e.getMessage(), e);
			logger.log(Level.SEVERE, "Couldn't create file " + exported.getName(), crudException);
		}

	}

}
