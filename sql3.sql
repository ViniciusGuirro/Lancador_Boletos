CREATE TABLE departamento (
  Id int(11) NOT NULL AUTO_INCREMENT,
  Name varchar(60) DEFAULT NULL,
  PRIMARY KEY (Id)
);

CREATE TABLE boleto (
  Id int(11) NOT NULL AUTO_INCREMENT,
  Name varchar(60) NOT NULL,
  Numero varchar(100) NOT NULL,
  Vencimento datetime(6) NOT NULL,
  Valor double NOT NULL,NameName
  DepartamentoId int(11) NOT NULL,
  PRIMARY KEY (Id),
  FOREIGN KEY (DepartamentoId) REFERENCES departamento (id)
);

INSERT INTO departamento (Name) VALUES 
  ('Despesas Loja'),
  ('Despesas Patrão'),
  ('Fornecedores');

INSERT INTO boleto (Name, Numero, Vencimento, Valor, DepartamentoId) VALUES 
  ('Computador','1485ga','1998-04-21 00:00:00',250,1),
  ('Cartao','859251a','1979-12-31 00:00:00',3500,2),
  ('Harald','5895ab','1988-01-15 00:00:00',2200,3);