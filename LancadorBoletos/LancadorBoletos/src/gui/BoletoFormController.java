package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangedListener;
import gui.util.Alertas;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Boleto;
import model.entities.Departamento;
import model.exceptions.ValidationException;
import model.services.BoletoService;
import model.services.DepartamentoService;

public class BoletoFormController implements Initializable {

	private Boleto entidade;

	private BoletoService service;

	private DepartamentoService departamentoService;

	private List<DataChangedListener> dataChangedListeners = new ArrayList<DataChangedListener>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtNumero;

	@FXML
	private DatePicker dpVencimento;

	@FXML
	private TextField txtValor;

	@FXML
	private ComboBox<Departamento> comboBoxDepartamento;

	@FXML
	private Label labelErrorName;

	@FXML
	private Label labelErrorNumero;

	@FXML
	private Label labelErrorVencimento;

	@FXML
	private Label labelErrorValor;

	@FXML
	private Button btSalvar;

	@FXML
	private Button btCancelar;

	private ObservableList<Departamento> obsList;

	public void setBoleto(Boleto entidade) {
		this.entidade = entidade;
	}

	public void setBoletoServices(BoletoService service, DepartamentoService departamentoService) {
		this.service = service;
		this.departamentoService = departamentoService;
	}

	public void subscribeDataChangedListener(DataChangedListener listener) {
		dataChangedListeners.add(listener);
	}

	public void loadAssociateObjects() {
		if (departamentoService == null) {
			throw new IllegalStateException("DepartamentoService estava nulo");
		}
		List<Departamento> list = departamentoService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartamento.setItems(obsList);
	}

	public void updateFormData() {
		if (entidade == null) {
			throw new IllegalStateException("Entidade vazia");
		}
		txtId.setText(String.valueOf(entidade.getId()));
		txtName.setText(entidade.getName());
		txtNumero.setText(entidade.getNumero());
		if (entidade.getVencimento() != null) {
			dpVencimento.setValue(LocalDate.ofInstant(entidade.getVencimento().toInstant(), ZoneId.systemDefault()));
		}
		Locale.setDefault(Locale.US);
		txtValor.setText(String.format("%.2f", entidade.getValor()));

		if (entidade.getDepartamento() == null) {
			comboBoxDepartamento.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartamento.setValue(entidade.getDepartamento());
		}
	}

	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if (entidade == null) {
			throw new IllegalStateException("Entidade nula");
		}
		if (service == null) {
			throw new IllegalStateException("Servico nulo");
		}
		try {
			entidade = getFormData();
			service.saveOrUpdate(entidade);
			Utils.currentStage(event).close();
			notifyDataChangedListeners();

		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		} catch (DbException e) {
			Alertas.showAlert("Erro ao salvar o objeto", null, e.getMessage(), AlertType.ERROR);
		}

	}

	private void notifyDataChangedListeners() {
		for (DataChangedListener listener : dataChangedListeners) {
			listener.onDataChanged();
		}
	}

	private Boleto getFormData() {
		Boleto obj = new Boleto();

		ValidationException exception = new ValidationException("Validations Error");

		obj.setId(Utils.tryPaInteger(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Campo n?o pode ser vazio");
		}
		obj.setName(txtName.getText());

		if (txtNumero.getText() == null || txtNumero.getText().trim().equals("")) {
			exception.addError("numero", "Campo n?o pode ser vazio");
		}
		obj.setNumero(txtNumero.getText());

		if (dpVencimento.getValue() == null) {
			exception.addError("Vencimento", "Campo n?o pode ser vazio");
		} else {
			Instant instant = Instant.from(dpVencimento.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setVencimento(Date.from(instant));
		}

		if (txtValor.getText() == null || txtValor.getText().trim().equals("")) {
			exception.addError("valor", "Campo n?o pode ser vazio");
		}
		obj.setValor(Utils.tryPaDouble(txtValor.getText()));
		
		obj.setDepartamento(comboBoxDepartamento.getValue());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		Constraints.setTextFieldDouble(txtValor);
		Constraints.setTextFieldMaxLength(txtNumero, 20);
		Utils.formatDatePicker(dpVencimento, "dd/MM/yyyy");
		initializeComboBoxDepartment();
	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
		else {
			labelErrorName.setText("");
		}

		if (fields.contains("numero")) {
			labelErrorNumero.setText(errors.get("numero"));
		}
		else {
			labelErrorNumero.setText("");
		}

		if (fields.contains("valor")) {
			labelErrorValor.setText(errors.get("valor"));
		}
		else {
			labelErrorValor.setText("");
		}
		
		if (fields.contains("vencimento")) {
			labelErrorVencimento.setText(errors.get("vencimento"));
		}
		else {
			labelErrorVencimento.setText("");
		}

	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartamento.setCellFactory(factory);
		comboBoxDepartamento.setButtonCell(factory.call(null));
	}
}
