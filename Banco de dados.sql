CREATE TABLE department (
  Id int(11) NOT NULL AUTO_INCREMENT,
  Name varchar(60) DEFAULT NULL,
  PRIMARY KEY (Id)
);

CREATE TABLE boleto (
  Id int(11) NOT NULL AUTO_INCREMENT,
  Name varchar(60) NOT NULL,
  Numero varchar(30) NOT NULL,
  Vencimento datetime(6) NOT NULL,
  Valor double NOT NULL,
  DepartmentId int(11) NOT NULL,
  PRIMARY KEY (Id),
  FOREIGN KEY (DepartmentId) REFERENCES department (id)
);

INSERT INTO department (Name) VALUES 
  ('Gastos Patrao'),
  ('Gastos Loja'),
  ('Compras Fornecedores');

INSERT INTO boleto (Name, Numero, Vencimento, Valor, DepartmentId) VALUES 
  ('Harald','2541j','2020-04-21 00:00:00',104,1),
  ('Nestle','sr418923','2019-12-31 00:00:00',530,1),
  ('Cartao','d54878','2020-01-15 00:00:00',200,2),
  ('Teclado','896b','2019-11-30 00:00:00',5080,3),
  ('Arcor','a47895','2020-01-09 00:00:00',50,1);
