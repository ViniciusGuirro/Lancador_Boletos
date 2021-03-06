package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangedListener;
import gui.util.Alertas;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Boleto;
import model.services.BoletoService;
import model.services.DepartamentoService;

public class BoletoListController implements Initializable, DataChangedListener {

	private BoletoService service;
	
	private DepartamentoService departamentoService;

	@FXML
	private TableView<Boleto> tableViewBoleto;

	@FXML
	private TableColumn<Boleto, Integer> tableColumnId;

	@FXML
	private TableColumn<Boleto, String> tableColumnName;
	
	@FXML
	private TableColumn<Boleto, String> tableColumnNumero;
	
	@FXML
	private TableColumn<Boleto, Date> tableColumnVencimento;
	
	@FXML
	private TableColumn<Boleto, Double> tableColumnValor;

	@FXML
	private TableColumn<Boleto, Boleto> tableColumnEDIT;

	@FXML
	TableColumn<Boleto, Boleto> tableColumnREMOVE;

	@FXML
	private Button btNovo;

	private ObservableList<Boleto> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Boleto obj = new Boleto();
		createDialogForm(obj, "/gui/BoletoForm.fxml", parentStage);
	}

	public void setBoletoServices(BoletoService service, DepartamentoService departamentoService) {
		this.service = service;
		this.departamentoService = departamentoService;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializeNodes();
	}

	private void inicializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		tableColumnNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
		tableColumnVencimento.setCellValueFactory(new PropertyValueFactory<>("vencimento"));
		Utils.formatTableColumnDate(tableColumnVencimento, "dd/MM/yyyy");
		tableColumnValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
		Utils.formatTableColumnDouble(tableColumnValor, 2);

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewBoleto.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Servico nulo");
		}
		List<Boleto> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewBoleto.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Boleto obj, String nameCompleto, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nameCompleto));
			Pane pane = loader.load();

			BoletoFormController controller = loader.getController();
			controller.setBoleto(obj);
			controller.setBoletoServices(new BoletoService(), new DepartamentoService());
			controller.loadAssociateObjects();
			controller.subscribeDataChangedListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados do Boleto");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alertas.showAlert("IO Exception", "Error ao carregar a view", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();

	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Boleto, Boleto>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Boleto obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/BoletoForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Boleto, Boleto>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Boleto obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Boleto obj) {
		Optional<ButtonType> result = Alertas.showConfirmation("Confirmacao", "Tem certeza que deseja remover?");
		
		if(result.get() == ButtonType.OK) {
			if(service == null) {
				throw new IllegalStateException("Servico nulo");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch(DbIntegrityException e) {
				Alertas.showAlert("Erro ao remover o objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
}
