/*
José Francisco González Ordoñez
IN5AV
fecha de creación: 
	17/04/2024
fechas de modificación:
	20/04/2024
    21/04/2024
    22/04/2024
*/
drop database if exists DBKinalStore2024;

create database DBKinalStore2024;
use DBKinalStore2024;

create table TipoProducto(
	codigoTipoProducto int not null auto_increment,
    descripcion varchar(45) not null,
    primary key PK_codigoTipoProducto (codigoTipoProducto)
);

create table Proveedores(
	codigoProveedor int not null auto_increment,
    NITProveedor varchar(10) not null,
    nombresProveedor varchar(60) not null,
    apellidosProveedor varchar(60) not null,
    direccionProveedor varchar(150) not null,
    razonSocial varchar(60) not null,
    contactoPrincipal varchar(100) not null,
    paginaWeb varchar(50) not null,
    primary key PK_codigoProveedor (codigoProveedor)
);

create table Compras(
	numeroDocumento int not null,
    fechaDocumento date not null,
    descripcion varchar(60) not null,
    totalDocumento decimal(10,2) default 0,
    primary key PK_numeroDocumento (numeroDocumento)
);

create table Clientes(
	codigoCliente int not null auto_increment,
    NITCliente varchar(10) not null,
    nombresCliente varchar(50) not null,
    apellidosCliente varchar(50) not null,
    direccionCliente varchar(150) not null,
    telefonoCliente varchar(8) not null,
    correoCliente varchar(50) not null,
    primary key PK_codigoCliente (codigoCliente)
);

create table CargoEmpleado(
	codigoCargoEmpleado int not null auto_increment,
    nombreCargo varchar(45) not null,
    descripcionCargo varchar(45) not null,
    primary key PK_codigoCargoEmpleado(codigoCargoEmpleado)
);

create table TelefonoProveedor(
	codigoTelefonoProveedor int not null auto_increment,
    numeroPrincipal varchar(8) not null,
    numeroSecundario varchar(8),
    observaciones varchar(45),
    codigoProveedor int not null,
    primary key PK_codigoTelefonoProveedor (codigoTelefonoProveedor),
    constraint FK_TelefonoProveedor_Proveedores foreign key (codigoProveedor) references Proveedores (codigoProveedor)
);

create table Productos(
	codigoProducto varchar(15) not null,
    descripcionProducto varchar(45) not null,
    precioUnitario decimal(10,2) default 0.00,
    precioDocena decimal(10,2) default 0.00,
    precioMayor decimal(10,2) default 0.00,
    imagenProducto varchar(255),
    existencia int default 0,
    codigoTipoProducto int not null,
    codigoProveedor int not null,
    primary key PK_codigoProducto (codigoProducto),
    constraint FK_Productos_TipoProducto foreign key (codigoTipoProducto) references TipoProducto (codigoTipoProducto),
    constraint FK_Productos_Proveedores foreign key (codigoProveedor) references Proveedores (codigoProveedor)
);

create table DetalleCompra(
	codigoDetalleCompra int not null auto_increment,
    costoUnitario decimal(10,2) not null,
    cantidad int not null,
    codigoProducto varchar(15) not null,
    numeroDocumento int not null,
    primary key PK_codigoDetalleCompra (codigoDetalleCompra),
    constraint FK_DetalleCompra_Productos foreign key(codigoProducto) references Productos (codigoProducto),
    constraint FK_DetalleCompra_Compras foreign key (numeroDocumento) references Compras (numeroDocumento)
);

create table EmailProveedor(
	codigoEmailProveedor int not null auto_increment,
    emailProveedor varchar(50) not null,
    descripcion varchar(100) not null,
    codigoProveedor int not null,
    primary key PK_codigoEmailProveedor (codigoEmailProveedor),
    constraint FK_EmailProveedor_Proveedores foreign key (codigoProveedor) references Proveedores (codigoProveedor)
);

create table Empleados(
	codigoEmpleado int not null,
    nombresEmpleado varchar(50)not null,
    apellidosEmpleado varchar(50) not null,
    sueldo decimal(10,2) not null,
    direccionEmpleado varchar(150) not null,
    turno varchar(15) not null,
    codigoCargoEmpleado int not null,
    primary key PK_codigoEmpleado (codigoEmpleado),
    constraint FK_Empleados_CargoEmpleado foreign key (codigoCargoEmpleado) references CargoEmpleado(codigoCargoEmpleado)
);

create table Factura(
	numeroFactura int not null,
    estado varchar(50) not null,
    totalFactura decimal(10,2),
    fechaFactura date not null,
    codigoCliente int not null,
    codigoEmpleado int not null,
    primary key PK_numeroFactura (numeroFactura),
    constraint FK_Factura_Clientes foreign key (codigoCliente) references Clientes (codigoCliente),
    constraint FK_Factura_Empleados foreign key (codigoEmpleado) references Empleados (codigoEmpleado)
);

create table DetalleFactura(
	codigoDetalleFactura int not null auto_increment,
    precioUnitario decimal(10,2),
    cantidad int not null,
    numeroFactura int not null,
    codigoProducto varchar (15) not null,
    primary key PK_codigoDetalleFactura (codigoDetalleFactura),
    constraint FK_DetalleFactura_Factura foreign key (numeroFactura) references Factura (numeroFactura),
    constraint FK_DetalleFactura_Productos foreign key (codigoProducto) references Productos (codigoProducto)
);

create table Usuario(
	codigoUsuario int not null auto_increment,
    nombreUsuario varchar(100) not null,
    apellidoUsuario varchar(100) not null,
    usuarologin varchar(50) not null,
    contrasena varchar(50) not null,
    primary key PK_codigoUsuario(codigoUsuario)
);

create table Login(
	usuarioMaster varchar(50) not null,
    passwordLogin varchar(50) not null,
    primary key PK_usuarioMaster(usuarioMaster)
);

-- Triggers
-- entidad De Compras
Delimiter $$
	create trigger tr_Compra_After_Insert
		after insert on DetalleCompra
        for each row
        begin
			declare totalDoc decimal(10,2) default 0.00;
            
            select SUM(costoUnitario * cantidad) into totalDoc
			from DetalleCompra
			where numeroDocumento = new.numeroDocumento;
            
            update Compras C
				set
					C.totalDocumento = totalDoc
				where C.numeroDocumento = new.numeroDocumento;
        end$$;
Delimiter ;

-- entidad de Productos

Delimiter $$
	create trigger tr_DetalleCompra_After_Insert
    After Insert on DetalleCompra
		for each row
		Begin
			declare resultdivi  decimal(10,2) default 0;
            declare total decimal(10,2) default 0.00;

			set total = (select totalDocumento from Compras where numeroDocumento = new.numeroDocumento limit 1);
            set resultdivi = (total/new.cantidad);
            
			Update Productos P
				set
					P.precioUnitario = (resultdivi * 0.40) + resultdivi, 
                    P.precioDocena = (resultdivi * 0.35) + resultdivi, 
                    P.precioMayor = (resultdivi * 0.25) + resultdivi,
					P.existencia = P.existencia + new.cantidad
				Where P.codigoProducto = new.codigoProducto;
		End$$
Delimiter ;
select * from Productos; 

-- entidad Detalle Factura
Delimiter $$
	create trigger tr_DetalleFactura_before_insert
	before insert on DetalleFactura
	for each row
        begin
			declare precio decimal(10,2) default 0.00;
			
			select precioUnitario into precio
			from Productos P
			where P.codigoProducto = new.codigoProducto;
			
			set NEW.precioUnitario = precio;
        end$$
Delimiter ;

-- entidad Factura
Delimiter $$
	create trigger tr_Factura_After_Insert
    after insert on DetalleFactura
    for each row
    begin
		declare totalF decimal(10,2) default 0.00;
        
        select SUM(precioUnitario * cantidad) into totalF
		from DetalleFactura DF
		where DF.numeroFactura = new.numeroFactura;
        
        update Factura F
			set 
				F.totalFactura = totalF
		where F.numeroFactura = new.numeroFactura;
    end$$;
Delimiter ;

-- Procedimientos almacenados de la entidad: Usuario

-- agregar Usuario
Delimiter $$
	create Procedure sp_AgregarUsuario(in nombreUsuario varchar(100), in apellidoUsuario varchar(100), in usuarologin varchar(50), in contrasena varchar(50))
		Begin
			Insert into Usuario (nombreUsuario, apellidoUsuario, usuarologin, contrasena) values (nombreUsuario, apellidoUsuario, usuarologin, contrasena);
        End$$
Delimiter ;

call sp_AgregarUsuario('José','González','jgonz','1234');
call sp_AgregarUsuario('Pedro','Armas','parmas','1234');

-- listar usuario
Delimiter $$
	create procedure sp_ListarUsuarios()
		begin
			select 
            U.codigoUsuario,
            U.nombreUsuario,
            U.apellidoUsuario,
            U.usuarologin,
            U.contrasena
				from Usuario U;
		end$$
Delimiter ;

call sp_ListarUsuarios();

-- Procedimientos almacenados de la entidad: Tipo Producto

-- Agregar
Delimiter $$
	create Procedure sp_AgregarTipoProducto(in descripcion varchar(45))
		Begin
			Insert into TipoProducto (descripcion) values (descripcion);
        End$$
Delimiter ;

call sp_AgregarTipoProducto('Electrodomesticos');
call sp_AgregarTipoProducto('Cocina');
call sp_AgregarTipoProducto('Ferreteria');
call sp_AgregarTipoProducto('Jardineria');
call sp_AgregarTipoProducto('Mecanica Automottriz');
call sp_AgregarTipoProducto('Ropa');
call sp_AgregarTipoProducto('Calzado');
call sp_AgregarTipoProducto('Deportes');
call sp_AgregarTipoProducto('Tecnología');
call sp_AgregarTipoProducto('Muebles');
call sp_AgregarTipoProducto('Limpieza');
call sp_AgregarTipoProducto('Bebidas');
call sp_AgregarTipoProducto('Alimentos');
call sp_AgregarTipoProducto('Juguetes');
call sp_AgregarTipoProducto('Papelería');

-- Listar
Delimiter $$
	create procedure sp_ListarTiposProductos()
		begin
			select 
            TP. codigoTipoProducto,
            TP.descripcion
				from TipoProducto TP;
		end$$
Delimiter ;

-- Buscar
Delimiter $$
	create procedure sp_BuscarTipoProducto(in codTipoProducto int)
		begin
			select 
				TP.codigoTipoProducto,
				TP.descripcion
			from TipoProducto TP
				where TP.codigoTipoProducto = codTipoProducto;
		end$$
Delimiter ;

-- Elimiar
Delimiter $$
	create procedure sp_ElimiarTipoProducto(in codTipoProducto int)
		begin
			delete
				from TipoProducto
					where TipoProducto.codigoTipoProducto = codTipoProducto;
		end$$
Delimiter ;


-- Editar
Delimiter $$
	create procedure sp_EditarTipoProducto(in codTipoProducto int, in descrip varchar(45))
		begin
			update TipoProducto TP
				set TP.descripcion = descrip
				where TP.codigoTipoProducto = codTipoProducto;
		end$$
Delimiter ;


-- Procedimientos almacenados de la entidad: Proveedores

-- Agregar
Delimiter $$
	create Procedure sp_AgregarProveedor(in NITProveedor varchar(10),in nombresProveedor varchar(60),in apellidosProveedor varchar(60),in direccionProveedor varchar(150),in razonSocial varchar(60),in contactoPrincipal varchar(100),in paginaWeb varchar(50))
		Begin
			Insert into Proveedores (NITProveedor, nombresProveedor, apellidosProveedor, direccionProveedor, razonSocial, contactoPrincipal, paginaWeb) values (NITProveedor, nombresProveedor, apellidosProveedor, direccionProveedor, razonSocial, contactoPrincipal, paginaWeb);
        End$$
Delimiter ;

call sp_AgregarProveedor('4596658-3', 'José', 'Gonzáles', 'Zona 10, Ciudad de Guatemala', 'Estudiante de Informatica "A"', '41379293', 'kinal.cademy');
call sp_AgregarProveedor('4587923-2', 'María', 'López', 'Zona 1, Ciudad de Guatemala', 'Profesora de Matemáticas', '52938475', 'escuela.math');
call sp_AgregarProveedor('4567891-4', 'Carlos', 'Martínez', 'Zona 5, Ciudad de Guatemala', 'Ingeniero Civil', '45782341', 'constructora.civil');
call sp_AgregarProveedor('4578123-6', 'Ana', 'Pérez', 'Zona 12, Ciudad de Guatemala', 'Arquitecta', '41789023', 'diseno.arq');
call sp_AgregarProveedor('4556789-1', 'Luis', 'Gómez', 'Zona 9, Ciudad de Guatemala', 'Contador Público', '48927364', 'contabilidad.gomez');
call sp_AgregarProveedor('4596721-7', 'Elena', 'Hernández', 'Zona 11, Ciudad de Guatemala', 'Doctora', '41023847', 'clinica.elena');
call sp_AgregarProveedor('4589345-3', 'Miguel', 'Rodríguez', 'Zona 3, Ciudad de Guatemala', 'Abogado', '52839475', 'bufete.legal');
call sp_AgregarProveedor('4598372-9', 'Patricia', 'Sánchez', 'Zona 15, Ciudad de Guatemala', 'Ingeniera Química', '41372984', 'quimica.industrial');
call sp_AgregarProveedor('4579123-4', 'Fernando', 'Ramírez', 'Zona 7, Ciudad de Guatemala', 'Administrador de Empresas', '41239875', 'admin.empresas');
call sp_AgregarProveedor('4567321-8', 'Gabriela', 'Torres', 'Zona 8, Ciudad de Guatemala', 'Veterinaria', '41928734', 'veterinaria.pets');
call sp_AgregarProveedor('4587192-5', 'Diego', 'Vargas', 'Zona 4, Ciudad de Guatemala', 'Chef', '41789324', 'gastronomia.diego');
call sp_AgregarProveedor('4598732-6', 'Laura', 'Mendoza', 'Zona 6, Ciudad de Guatemala', 'Diseñadora Gráfica', '41567293', 'diseno.grafico');
call sp_AgregarProveedor('4596183-7', 'Ricardo', 'Ortiz', 'Zona 13, Ciudad de Guatemala', 'Periodista', '41378295', 'prensa.ricardo');
call sp_AgregarProveedor('4597321-4', 'Carmen', 'Morales', 'Zona 2, Ciudad de Guatemala', 'Psicóloga', '41678234', 'consulta.psico');
call sp_AgregarProveedor('4578345-2', 'Hugo', 'Flores', 'Zona 14, Ciudad de Guatemala', 'Mecánico', '41482395', 'taller.mecanico');

-- Listar
Delimiter $$
	create procedure sp_ListarProveedores()
		begin
			select 
				P.codigoProveedor,
				P.NITProveedor, 
                P.nombresProveedor, 
                P.apellidosProveedor, 
                P.direccionProveedor, 
                P.razonSocial, 
                P.contactoPrincipal, 
                P.paginaWeb
				from  Proveedores P;
		end$$
Delimiter ;


-- Buscar
Delimiter $$
	create procedure sp_BuscarProveedor(in codoProveedor int)
		begin
			select 
				P.codigoProveedor,
				P.NITProveedor, 
                P.nombresProveedor, 
                P.apellidosProveedor, 
                P.direccionProveedor, 
                P.razonSocial, 
                P.contactoPrincipal, 
                P.paginaWeb
			from  Proveedores P
				where P.codigoProveedor = codoProveedor;
		end$$
Delimiter ;

-- Elimiar
Delimiter $$
	create procedure sp_ElimiarProveedor(in codoProveedor int)
		begin
			delete
				from Proveedores 
					where Proveedores.codigoProveedor = codoProveedor;
		end$$
Delimiter ;

-- Editar
Delimiter $$
	create procedure sp_EditarProveedor(in codProveedor int, in NTProveedor varchar(10),in nomProveedor varchar(60),in apeProveedor varchar(60),in direcProveedor varchar(150),in raSocial varchar(60),in contactoPrincipl varchar(100),in pagWeb varchar(50))
		begin
			update  Proveedores P
				set P.NITProveedor = NTProveedor, 
					P.nombresProveedor = nomProveedor, 
                    P.apellidosProveedor = apeProveedor, 
                    P.direccionProveedor = direcProveedor, 
                    P.razonSocial = raSocial, 
                    P.contactoPrincipal = contactoPrincipl, 
                    P.paginaWeb = pagWeb
				where P.codigoProveedor = P.codProveedor;
		end$$
Delimiter ;

-- Procedimientos almacenados de la entidad: Compras

-- Agregar
Delimiter $$
	create Procedure sp_AgregarCompra(in numeroDocumento int, in fechaDocumento date, in descripcion varchar(60))
		Begin
			Insert into Compras (numeroDocumento, fechaDocumento, descripcion) values (numeroDocumento, fechaDocumento, descripcion);
        End$$
Delimiter ;

call sp_AgregarCompra(199, '2024-06-01', 'Compra realizada con exito');
call sp_AgregarCompra(200, '2024-06-02', 'Compra realizada con exito');
call sp_AgregarCompra(201, '2024-06-03', 'Compra realizada con exito');
call sp_AgregarCompra(202, '2024-06-04', 'Compra realizada con exito');
call sp_AgregarCompra(203, '2024-06-05', 'Compra realizada con exito');
call sp_AgregarCompra(204, '2024-06-06', 'Compra realizada con exito');
call sp_AgregarCompra(205, '2024-06-07', 'Compra realizada con exito');
call sp_AgregarCompra(206, '2024-06-08', 'Compra realizada con exito');
call sp_AgregarCompra(207, '2024-06-09', 'Compra realizada con exito');
call sp_AgregarCompra(208, '2024-06-10', 'Compra realizada con exito');
call sp_AgregarCompra(209, '2024-06-11', 'Compra realizada con exito');
call sp_AgregarCompra(210, '2024-06-12', 'Compra realizada con exito');
call sp_AgregarCompra(211, '2024-06-13', 'Compra realizada con exito');
call sp_AgregarCompra(212, '2024-06-14', 'Compra realizada con exito');
call sp_AgregarCompra(213, '2024-06-15', 'Compra realizada con exito');


-- Listar
Delimiter $$
	create procedure sp_ListarCompras()
		begin
			select 
				C.numeroDocumento, 
                C.fechaDocumento, 
                C.descripcion, 
                C.totalDocumento
			from Compras C;
		end$$
Delimiter ;

call sp_ListarCompras();

-- Buscar
Delimiter $$
	create procedure sp_BuscarCompra(in numeroDoc int)
		begin
			select 
				C.numeroDocumento,
				C.fechaDocumento, 
                C.descripcion, 
                C.totalDocumento
			from  Compras C
				where C.numeroDocumento = numeroDoc;
		end$$
Delimiter ;

-- Elimiar
Delimiter $$
	create procedure sp_ElimiarCompra(in numDoc int)
		begin
			delete
				from Compras
					where Compras.numeroDocumento = numDoc;
		end$$
Delimiter ;

-- Editar
Delimiter $$
	create procedure sp_EditarCompra(in nDoc int, in fechDoc date, in descrip varchar(60), in totalDoc decimal(10,2))
		begin
			update Compras C 
				set C.fechaDocumento = fechDoc,
					C.descripcion = descrip,
                    C.totalDocumento = totalDoc
				where C.numeroDocumento = nDoc;
		end$$
Delimiter ;


-- Procedimientos almacenados de la entidad: Clientes

-- Agregar
Delimiter $$
	create Procedure sp_AgregarCliente(in NITCliente varchar(10), in nombresCliente varchar(50), in apellidosCliente varchar(50), in direccionCliente varchar(150), in telefonoCliente varchar(8), in correoCliente varchar(50))
		Begin
			Insert into Clientes (NITCliente, nombresCliente, apellidosCliente, direccionCliente, telefonoCliente, correoCliente) values (NITCliente, nombresCliente, apellidosCliente, direccionCliente, telefonoCliente, correoCliente);
        End$$
Delimiter ;

call sp_AgregarCliente('4596658-3', 'José Francisco', 'González Ordoñez', '6 avenida 13-54 zona 7, Colonia Landívar, 01007 Ciudad de Guatemala,', '51349293', 'jgonzalez-2023195@gmail.com');
call sp_AgregarCliente('1234567-8', 'Ana María', 'Martínez López', '8 calle 12-34 zona 1, Centro Histórico, 01001 Ciudad de Guatemala', '55567890', 'amartinez-2023123@gmail.com');
call sp_AgregarCliente('8765432-1', 'Luis Alberto', 'Pérez Gómez', '4 avenida 9-45 zona 12, Colonia El Carmen, 01012 Ciudad de Guatemala', '44556677', 'lperez-2023245@gmail.com');
call sp_AgregarCliente('5678901-2', 'María José', 'López Ramírez', '7 avenida 10-23 zona 5, Colonia El Incienso, 01005 Ciudad de Guatemala', '99887766', 'mlopez-2023356@gmail.com');
call sp_AgregarCliente('2345678-9', 'Carlos Eduardo', 'Hernández Castillo', '5 calle 11-67 zona 6, Colonia Atlántida, 01006 Ciudad de Guatemala', '11223344', 'chernandez-2023467@gmail.com');
call sp_AgregarCliente('3456789-0', 'Lucía Fernanda', 'Ramírez Ortiz', '3 avenida 14-12 zona 7, Colonia Roosevelt, 01007 Ciudad de Guatemala', '22334455', 'lramirez-2023578@gmail.com');
call sp_AgregarCliente('4567890-1', 'Pedro Antonio', 'Sánchez Morales', '2 calle 15-23 zona 8, Colonia El Trébol, 01008 Ciudad de Guatemala', '33445566', 'psanchez-2023689@gmail.com');
call sp_AgregarCliente('5678901-3', 'Laura Sofía', 'Gómez Pérez', '9 avenida 16-34 zona 9, Colonia La Reformita, 01009 Ciudad de Guatemala', '44556677', 'lgomez-2023790@gmail.com');
call sp_AgregarCliente('6789012-4', 'Jorge Luis', 'Morales Herrera', '10 calle 17-45 zona 11, Colonia Mariscal, 01011 Ciudad de Guatemala', '55667788', 'jmorales-2023801@gmail.com');
call sp_AgregarCliente('7890123-5', 'Sara Isabel', 'Rodríguez Fernández', '11 avenida 18-56 zona 13, Colonia Las Américas, 01013 Ciudad de Guatemala', '66778899', 'srodriguez-2023912@gmail.com');
call sp_AgregarCliente('4596658-13', 'Juan Carlos', 'Vargas Reyes', '16 avenida 23-74 zona 11, Colonia La Merced, 01028 Ciudad de Guatemala,', '61234567', 'jvargas-2023210@gmail.com');
call sp_AgregarCliente('4596658-14', 'Sofía Valentina', 'Mendoza Castillo', '17 avenida 24-83 zona 12, Colonia La Esperanza, 01030 Ciudad de Guatemala,', '62345678', 'smendoza-2023211@gmail.com');
call sp_AgregarCliente('4596658-15', 'Pablo Andrés', 'Silva Torres', '18 avenida 25-92 zona 13, Colonia San Francisco, 01032 Ciudad de Guatemala,', '63456789', 'psilva-2023212@gmail.com');
call sp_AgregarCliente('4596658-16', 'Gabriela María', 'Navarro Sánchez', '19 avenida 26-101 zona 14, Colonia San Antonio, 01034 Ciudad de Guatemala,', '64567890', 'gnavarro-2023213@gmail.com');
call sp_AgregarCliente('4596658-17', 'Fernando José', 'Ortega Ramírez', '20 avenida 27-110 zona 15, Colonia San Miguel, 01036 Ciudad de Guatemala,', '65678901', 'fortega-2023214@gmail.com');


-- Listar
Delimiter $$
	create procedure sp_ListarClientes()
		begin
			select 
            C.codigoCliente, 
            C.NITCliente, 
            C.nombresCliente, 
            C.apellidosCliente, 
            C.direccionCliente, 
            C.telefonoCliente, 
            C.correoCliente
				from Clientes C;
		end$$
Delimiter ;

call sp_ListarClientes();

-- Buscar
Delimiter $$
	create procedure sp_BuscarCliente(in codigCliente int)
		begin
			select 
				C.codigoCliente, 
				C.NITCliente, 
				C.nombresCliente, 
				C.apellidosCliente, 
				C.direccionCliente, 
				C.telefonoCliente, 
				C.correoCliente
			from Clientes C
				where codigoCliente = codigCliente;
		end$$
Delimiter ;

-- Elimiar
Delimiter $$
	create procedure sp_EliminarCliente(in codCliente int)
		Begin
			Delete from Clientes 
				Where Clientes.codigoCliente = codCliente;
		End$$
Delimiter ;

-- Editar
Delimiter $$
	create procedure sp_EditarClientes(in codCliente int, in NITClient varchar(10), in nombresClient varchar(50), in apellidosClient varchar(50), in direccionClient varchar(150), in telfonoCliente varchar(8), in coreoCliente varchar(50))
		begin
			update Clientes C
				set C.NITCliente = NITClient,
					C.nombresCliente = nombresClient,
                    C.apellidosCliente = apellidosClient,
                    C.direccionCliente = direccionClient,
					C.telefonoCliente = telfonoCliente,
					C.correoCliente = coreoCliente
				where C.codigoCliente = codCliente;
		end$$
Delimiter ;


-- Procedimientos almacenados de la entidad: CargoEmpleado

-- Agregar
Delimiter $$
	create Procedure sp_AgregarCargoEmpleado(in nombreCargo varchar(45), in descripcionCargo varchar(45))
		Begin
			Insert into CargoEmpleado (nombreCargo, descripcionCargo) values (nombreCargo, descripcionCargo);
        End$$
Delimiter ;

call sp_AgregarCargoEmpleado('Gerente General', 'Encargado del supermercado');
call sp_AgregarCargoEmpleado('Subgerente', 'Asistente del gerente general');
call sp_AgregarCargoEmpleado('Jefe de Ventas', 'Responsable de las ventas y promociones');
call sp_AgregarCargoEmpleado('Jefe de Compras', 'Encargado de la adquisición de productos');
call sp_AgregarCargoEmpleado('Jefe de Recursos Humanos', 'Responsable del personal y contratación');
call sp_AgregarCargoEmpleado('Jefe de Logística', 'Encargado de la distribución y almacenamiento');
call sp_AgregarCargoEmpleado('Jefe de Marketing', 'Responsable de la publicidad y marketing');
call sp_AgregarCargoEmpleado('Contador', 'Encargado de las finanzas y contabilidad');
call sp_AgregarCargoEmpleado('Cajero', 'Responsable de la caja y atención al cliente');
call sp_AgregarCargoEmpleado('Vendedor', 'Encargado de la atención al cliente');
call sp_AgregarCargoEmpleado('Supervisor de Tienda', 'Supervisa la tienda');
call sp_AgregarCargoEmpleado('Reponedor', 'Encargado de colocar productos');
call sp_AgregarCargoEmpleado('Auxiliar de Limpieza', 'Responsable del mantenimiento del área');
call sp_AgregarCargoEmpleado('Seguridad', 'Encargado de la seguridad del supermercado');
call sp_AgregarCargoEmpleado('Asistente Administrativo', 'Asiste en tareas administrativas y de oficina');

-- Listar
Delimiter $$
	create procedure sp_ListarCargosEmpleados()
		begin
			select 
				CE.codigoCargoEmpleado, 
                CE.nombreCargo, 
                CE.descripcionCargo
			from CargoEmpleado CE;
		end$$
Delimiter ;



-- Buscar
Delimiter $$
	create procedure sp_BuscarCargoEmpleado(in codCargoEmpleado int)
		begin
			select 
				CE.codigoCargoEmpleado, 
                CE.nombreCargo, 
                CE.descripcionCargo
			from CargoEmpleado CE
				where CE.codigoCargoEmpleado = codCargoEmpleado;
		end$$
Delimiter ;

-- Elimiar
Delimiter $$
	create procedure sp_ElimiarCargoEmpleado(in codCargoEmpleo int)
		begin
			delete
				from CargoEmpleado
					where CargoEmpleado.codigoCargoEmpleado = codCargoEmpleo;
		end$$
Delimiter ;

-- Editar
Delimiter $$
	create procedure sp_EditarCargoEmpleado(in codCgoEmpleo int, in nomCargo varchar(45), in descripCgo varchar(45))
		begin
			update CargoEmpleado CE
				set CE.nombreCargo = nomCargo,
					CE.descripcionCargo = descripCgo
				where CE.codigoCargoEmpleado = codCgoEmpleo;
		end$$
Delimiter ;

-- Procedimientos almacenados de la entidad: TelefonoProveedor

-- Agregar
Delimiter $$
	create Procedure sp_AgregarTelefonoProveedor(in numeroPrincipal varchar(8), in numeroSecundario varchar(8), in observaciones varchar(45), in codigoProveedor int)
		Begin
			Insert into TelefonoProveedor (numeroPrincipal, numeroSecundario, observaciones, codigoProveedor) values (numeroPrincipal, numeroSecundario, observaciones, codigoProveedor);
        End$$
Delimiter ;

call sp_AgregarTelefonoProveedor('41379293', null, 'Unico Número de celular', 1);
call sp_AgregarTelefonoProveedor('52938475', '41567892', 'Teléfono Principal', 2);
call sp_AgregarTelefonoProveedor('45782341', '45678923', 'Teléfono Principal', 3);
call sp_AgregarTelefonoProveedor('41789023', null, 'Unico Número de celular', 4);
call sp_AgregarTelefonoProveedor('48927364', '49827364', 'Teléfono Principal', 5);
call sp_AgregarTelefonoProveedor('41023847', '41568792', 'Teléfono Principal', 6);
call sp_AgregarTelefonoProveedor('52839475', null, 'Unico Número de celular', 7);
call sp_AgregarTelefonoProveedor('41372984', '41372985', 'Teléfono Principal', 8);
call sp_AgregarTelefonoProveedor('41239875', null, 'Unico Número de celular', 9);
call sp_AgregarTelefonoProveedor('41928734', '41673829', 'Teléfono Principal', 10);
call sp_AgregarTelefonoProveedor('41789324', '41789325', 'Teléfono Principal', 11);
call sp_AgregarTelefonoProveedor('41567293', '41567294', 'Teléfono Principal', 12);
call sp_AgregarTelefonoProveedor('41378295', null, 'Unico Número de celular', 13);
call sp_AgregarTelefonoProveedor('41678234', '41678235', 'Teléfono Principal', 14);
call sp_AgregarTelefonoProveedor('41482395', null, 'Unico Número de celular', 15);


-- Listar
Delimiter $$
	create procedure sp_ListarTelefonoProveedores()
		begin
			select 
				TP.codigoTelefonoProveedor, 
                TP.numeroPrincipal, 
                TP.numeroSecundario, 
                TP.observaciones, 
                TP.codigoProveedor
			from TelefonoProveedor TP;
		end$$
Delimiter ;

call sp_ListarTelefonoProveedores();

-- Buscar
Delimiter $$
	create procedure sp_BuscarTelefonoProveedor(in codTelefonoProveedor int)
		begin
			select 
				TP.codigoTelefonoProveedor, 
                TP.numeroPrincipal, 
                TP.numeroSecundario, 
                TP.observaciones, 
                TP.codigoProveedor
			from TelefonoProveedor TP 
				where TP.codigoTelefonoProveedor = codTelefonoProveedor;
		end$$
Delimiter ;

-- Elimiar
Delimiter $$
	create procedure sp_ElimiarTelefonoProveedor(in codTelProveedor int)
		begin
			delete
				from TelefonoProveedor
					where TelefonoProveedor.codigoTelefonoProveedor = codTelProveedor;
		end$$
Delimiter ;

-- Editar
Delimiter $$
	create procedure sp_EditarTelefonoProveedor(in codTelPro int, in numPrincipal varchar(8), in numSecundario varchar(8),in obser varchar(45), in codProveedor int)
		begin
			update TelefonoProveedor TP 
				set TP.numeroPrincipal = numPrincipal,
					TP.numeroSecundario = numSecundario,
                    TP.observaciones = obser,
                    TP.codigoProveedor = codProveedor
				where TP.codigoTelefonoProveedor = codTelPro;
		end$$
Delimiter ;

-- Procedimientos almacenados de la entidad: Productos

-- Agregar
Delimiter $$
	create Procedure sp_AgregarProducto(in codigoProducto varchar(15), in descripcionProducto varchar(45),in imagenProducto varchar(255), in existencia int, in codigoTipoProducto int, in codigoProveedor int)
		Begin
			Insert into Productos (codigoProducto, descripcionProducto, imagenProducto, existencia, codigoTipoProducto, codigoProveedor) values (codigoProducto, descripcionProducto, imagenProducto, existencia, codigoTipoProducto, codigoProveedor);
        End$$
Delimiter ;

call sp_AgregarProducto('1AA', 'Adquisición Licuadoras', null, 10, 1, 1);
call sp_AgregarProducto('2BB', 'Adquisición Refrigeradoras', null, 15, 2, 2);
call sp_AgregarProducto('3CC', 'Adquisición Microondas', null, 20, 3, 3);
call sp_AgregarProducto('4DD', 'Adquisición Lavadoras', null, 25, 4, 4);
call sp_AgregarProducto('5EE', 'Adquisición Secadoras', null, 30, 5, 5);
call sp_AgregarProducto('6FF', 'Adquisición Televisores', null, 35, 6, 6);
call sp_AgregarProducto('7GG', 'Adquisición Computadoras', null, 40, 7, 7);
call sp_AgregarProducto('8HH', 'Adquisición Tablets', null, 45, 8, 8);
call sp_AgregarProducto('9II', 'Adquisición Teléfonos', null, 50, 9, 9);
call sp_AgregarProducto('10JJ', 'Adquisición Cámaras', null, 55, 10, 10);
call sp_AgregarProducto('11KK', 'Adquisición Consolas', null, 60, 11, 11);
call sp_AgregarProducto('12LL', 'Adquisición Videoproyectores', null, 65, 12, 12);
call sp_AgregarProducto('13MM', 'Adquisición Impresoras', null, 70, 13, 13);
call sp_AgregarProducto('14NN', 'Adquisición Escáneres', null, 75, 14, 14);
call sp_AgregarProducto('15OO', 'Adquisición Calculadoras', null, 80, 15, 15);

-- Listar
Delimiter $$
	create procedure sp_ListarProductos()
		begin
			select 
				P.codigoProducto, 
                P.descripcionProducto, 
                P.precioUnitario, 
                P.precioDocena, 
                P.precioMayor, 
                P.imagenProducto, 
                P.existencia, 
                P.codigoTipoProducto, 
                P.codigoProveedor
			from Productos P;
		end$$
Delimiter ;

call sp_ListarProductos();

-- Buscar
Delimiter $$
	create procedure sp_BuscarProducto(in codProducto varchar(15))
		begin
			select 
				P.codigoProducto, 
                P.descripcionProducto, 
                P.precioUnitario, 
                P.precioDocena, 
                P.precioMayor, 
                P.imagenProducto, 
                P.existencia, 
                P.codigoTipoProducto, 
                P.codigoProveedor
			from Productos P 
				where P.codigoProducto = codProducto;
		end$$
Delimiter ;


-- Elimiar
Delimiter $$
	create procedure sp_ElimiarProducto(in codProduc varchar(15))
		begin
			delete
				from Productos
					where Productos.codigoProducto = codProduc;
		end$$
Delimiter ;

-- Editar
Delimiter $$
	create procedure sp_EditarProducto(in codPro varchar(15), in descripProducto varchar(45), in imagePro varchar(255), in precioU decimal(10,2), in precioD decimal(10,2), in precioM decimal(10,2), in exis int)
		begin
			update  Productos P 
				set P.descripcionProducto = descripProducto,
                    P.imagenProducto = imagePro,
                    P.precioUnitario = precioU,
                    P.precioDocena = precioD,
                    P.precioMayor = precioM,
                    P.existencia = exis
				where P.codigoProducto = codPro;
		end$$
Delimiter ;

-- Procedimientos almacenados de la entidad: DetalleCompra

-- Agregar
Delimiter $$
	create Procedure sp_AgregarDetalleCompra(in costoUnitario decimal(10,2), in cantidad int, in codigoProducto varchar(45), in numeroDocumento int)
		Begin
			Insert into DetalleCompra(costoUnitario, cantidad, codigoProducto, numeroDocumento) values (costoUnitario, cantidad, codigoProducto, numeroDocumento);
        End$$
Delimiter ;

call sp_AgregarDetalleCompra('500.50', 5, '1AA', 199);
call sp_AgregarDetalleCompra('750.25', 8, '2BB', 200);
call sp_AgregarDetalleCompra('320.75', 3, '3CC', 201);
call sp_AgregarDetalleCompra('600.00', 6, '4DD', 202);
call sp_AgregarDetalleCompra('400.50', 4, '5EE', 203);
call sp_AgregarDetalleCompra('900.20', 9, '6FF', 204);
call sp_AgregarDetalleCompra('200.30', 2, '7GG', 205);
call sp_AgregarDetalleCompra('850.75', 7, '8HH', 206);
call sp_AgregarDetalleCompra('550.60', 5, '9II', 207);
call sp_AgregarDetalleCompra('300.45', 3, '10JJ', 208);
call sp_AgregarDetalleCompra('720.90', 6, '11KK', 209);
call sp_AgregarDetalleCompra('430.25', 4, '12LL', 210);
call sp_AgregarDetalleCompra('610.80', 6, '13MM', 211);
call sp_AgregarDetalleCompra('380.70', 3, '14NN', 212);
call sp_AgregarDetalleCompra('950.40', 8, '15OO', 213);

-- Listar
Delimiter $$
	create procedure sp_ListarDetallesCompras()
		begin
			select 
				DC.codigoDetalleCompra, 
                DC.costoUnitario, 
                DC.cantidad, 
                DC.codigoProducto, 
                DC.numeroDocumento
			from DetalleCompra DC;
		end$$
Delimiter ;


-- Buscar
Delimiter $$
	create procedure sp_BuscarDetalleCompra(in codDetalleCompra int)
		begin
			select 
				DC.codigoDetalleCompra, 
                DC.costoUnitario, 
                DC.cantidad, 
                DC.codigoProducto, 
                DC.numeroDocumento
			from DetalleCompra DC
				where DC.codigoDetalleCompra = DC.codDetalleCompra;
		end$$
Delimiter ;

-- Elimiar
Delimiter $$
	create procedure sp_ElimiarDetalleCompra(in codDetaleCompra int)
		begin
			delete
				from DetalleCompra
					where DetalleCompra.codigoDetalleCompra = codDetaleCompra;
		end$$
Delimiter ;

-- Editar
Delimiter $$
	create procedure sp_EditarDetalleCompra(in codDetaleCom int, in costoU decimal(10,2), in cant int)
		begin
			update DetalleCompra DC
				set DC.costoUnitario = costoU,
					DC.cantidad = cant
				where DC.codigoDetalleCompra = codDetaleCom;
		end$$
Delimiter ;

-- Procedimientos almacenados de la entidad: EmailProveedor

-- Agregar
Delimiter $$
	create Procedure sp_AgregarEmailProveedor(in emailProveedor varchar(50),in descripcion varchar(100), in codigoProveedor int)
		Begin
			Insert into EmailProveedor(emailProveedor, descripcion, codigoProveedor) values (emailProveedor, descripcion, codigoProveedor);
        End$$
Delimiter ;

call sp_AgregarEmailProveedor('jgonzalez-2023195@kina.eud.gt', 'correo institucional', 1);
call sp_AgregarEmailProveedor('mlopez@escuela.math', 'correo institucional', 2);
call sp_AgregarEmailProveedor('cmartinez@constructora.civil', 'correo institucional', 3);
call sp_AgregarEmailProveedor('aperez@diseno.arq', 'correo institucional', 4);
call sp_AgregarEmailProveedor('lgomez@contabilidad.gomez', 'correo institucional', 5);
call sp_AgregarEmailProveedor('ehernandez@clinica.elena', 'correo institucional', 6);
call sp_AgregarEmailProveedor('mrodriguez@bufete.legal', 'correo institucional', 7);
call sp_AgregarEmailProveedor('psanchez@quimica.industrial', 'correo institucional', 8);
call sp_AgregarEmailProveedor('framirez@admin.empresas', 'correo institucional', 9);
call sp_AgregarEmailProveedor('gtorres@veterinaria.pets', 'correo institucional', 10);
call sp_AgregarEmailProveedor('dvargas@gastronomia.diego', 'correo institucional', 11);
call sp_AgregarEmailProveedor('lmendoza@diseno.grafico', 'correo institucional', 12);
call sp_AgregarEmailProveedor('rortiz@prensa.ricardo', 'correo institucional', 13);
call sp_AgregarEmailProveedor('cmorales@consulta.psico', 'correo institucional', 14);
call sp_AgregarEmailProveedor('hflores@taller.mecanico', 'correo institucional', 15);


-- Listar
Delimiter $$
	create procedure sp_ListarEmailsProveedores()
		begin
			select 
				EP.codigoEmailProveedor, 
                EP.emailProveedor, 
                EP.descripcion, 
                EP.codigoProveedor
			from EmailProveedor EP;
		end$$
Delimiter ;



-- Buscar
Delimiter $$
	create procedure sp_BuscarEmailProveedor(in codEmailProveedor int)
		begin
			select 
				EP.codigoEmailProveedor, 
                EP.emailProveedor, 
                EP.descripcion, 
                EP.codigoProveedor
			from EmailProveedor EP  
				where EP.codigoEmailProveedor = EP.codEmailProveedor;
		end$$
Delimiter ;

-- Elimiar
Delimiter $$
	create procedure sp_ElimiarEmailProveedor(in codEmalProveedor int)
		begin
			delete
				from EmailProveedor
					where EmailProveedor.codigoEmailProveedor = codEmalProveedor;
		end$$
Delimiter ;


-- Editar
Delimiter $$
	create procedure sp_EditarEmailProveedor(in codEmilProedor int, in emProveedor varchar(50), in descrip varchar(100))
		begin
			update  EmailProveedor EP
				set EP.emailProveedor = emProveedor,
					EP.descripcion = descrip
				where EP.codigoEmailProveedor = codEmilProedor;
		end$$
Delimiter ;

-- Procedimientos almacenados de la entidad: Empleados

-- Agregar
Delimiter $$
	create Procedure sp_AgregarEmpleado(in codigoEmpleado int, in nombresEmpleado varchar(50),in apellidosEmpleado varchar(50), in sueldo decimal(10,2), in direccionEmpleado varchar(150), in turno varchar(150), in codigoCargoEmpleado int)
		Begin
			Insert into Empleados (codigoEmpleado, nombresEmpleado, apellidosEmpleado, sueldo, direccionEmpleado, turno, codigoCargoEmpleado) values (codigoEmpleado, nombresEmpleado, apellidosEmpleado, sueldo, direccionEmpleado, turno, codigoCargoEmpleado);
        End$$
Delimiter ;

call sp_AgregarEmpleado(195, 'José', 'González', '4800.00', 'Zona 7, colonia Landivar, Guatemala, Guatemala', 'Diurno', 1);
call sp_AgregarEmpleado(196, 'María', 'López', '5000.00', 'Zona 1, Ciudad de Guatemala', 'Nocturno', 2);
call sp_AgregarEmpleado(197, 'Carlos', 'Martínez', '5200.00', 'Zona 5, Ciudad de Guatemala', 'Diurno', 3);
call sp_AgregarEmpleado(198, 'Ana', 'Pérez', '4900.00', 'Zona 12, Ciudad de Guatemala', 'Nocturno', 4);
call sp_AgregarEmpleado(199, 'Luis', 'Gómez', '5100.00', 'Zona 9, Ciudad de Guatemala', 'Diurno', 5);
call sp_AgregarEmpleado(200, 'Elena', 'Hernández', '4900.00', 'Zona 11, Ciudad de Guatemala', 'Nocturno', 6);
call sp_AgregarEmpleado(201, 'Miguel', 'Rodríguez', '5300.00', 'Zona 3, Ciudad de Guatemala', 'Diurno', 7);
call sp_AgregarEmpleado(202, 'Patricia', 'Sánchez', '5200.00', 'Zona 15, Ciudad de Guatemala', 'Nocturno', 8);
call sp_AgregarEmpleado(203, 'Fernando', 'Ramírez', '5000.00', 'Zona 7, Ciudad de Guatemala', 'Diurno', 9);
call sp_AgregarEmpleado(204, 'Gabriela', 'Torres', '4800.00', 'Zona 8, Ciudad de Guatemala', 'Nocturno', 10);
call sp_AgregarEmpleado(205, 'Diego', 'Vargas', '5400.00', 'Zona 4, Ciudad de Guatemala', 'Diurno', 11);
call sp_AgregarEmpleado(206, 'Laura', 'Mendoza', '5100.00', 'Zona 6, Ciudad de Guatemala', 'Nocturno', 12);
call sp_AgregarEmpleado(207, 'Ricardo', 'Ortiz', '5000.00', 'Zona 13, Ciudad de Guatemala', 'Diurno', 13);
call sp_AgregarEmpleado(208, 'Carmen', 'Morales', '5200.00', 'Zona 2, Ciudad de Guatemala', 'Nocturno', 14);
call sp_AgregarEmpleado(209, 'Hugo', 'Flores', '4900.00', 'Zona 14, Ciudad de Guatemala', 'Diurno', 15);

-- Listar
Delimiter $$
	create procedure sp_ListarEmpleados()
		begin
			select 
				E.codigoEmpleado, 
                E.nombresEmpleado, 
                E.apellidosEmpleado, 
                E.sueldo, 
                E.direccionEmpleado, 
                E.turno, 
                E.codigoCargoEmpleado
			from Empleados E;
		end$$
Delimiter ;

-- Buscar
Delimiter $$
	create procedure sp_BuscarEmpleado(in codEmpleado int)
		begin
			select 
				E.codigoEmpleado, 
                E.nombresEmpleado, 
                E.apellidosEmpleado, 
                E.sueldo, 
                E.direccionEmpleado, 
                E.turno, 
                E.codigoCargoEmpleado
			from Empleados E
				where E.codigoEmpleado = codEmpleado;
		end$$
Delimiter ;

-- Elimiar
Delimiter $$
	create procedure sp_ElimiarEmpleado(in codEmpleado int)
		begin
			delete
				from Empleados
					where Empleados.codigoEmpleado = codEmpleado;
		end$$
Delimiter ;


-- Editar
Delimiter $$
	create procedure sp_EditarEmpleado(in codiEmplo int, in nombreEmpleado varchar(50),in apellidoEmpleado varchar(50), in suelo decimal(10,2), in direcEmpleado varchar(150), in tulno varchar(150))
		begin
			update  Empleados E
				set E.nombresEmpleado = nombreEmpleado,
					E.apellidosEmpleado = apellidoEmpleado,
                    E.sueldo = suelo,
                    E.direccionEmpleado = direcEmpleado,
                    E.turno = tulno
				where E.codigoEmpleado = codiEmplo;
		end$$
Delimiter ;

-- Procedimientos almacenados de la entidad: Factura

-- Agregar
Delimiter $
	create Procedure sp_AgregarFactura(in numeroFactura int, in estado varchar(50), in fechaFactura date, in codigoCliente int, in codigoEmpleado int)
		Begin
			Insert into Factura (numeroFactura, estado, fechaFactura, codigoCliente, codigoEmpleado) values (numeroFactura, estado, fechaFactura, codigoCliente, codigoEmpleado);
        End$$
Delimiter ;

call sp_AgregarFactura(301, 'Pendiente', '2024-06-01', 2, 196);
call sp_AgregarFactura(302, 'Pagada', '2024-06-02', 3, 197);
call sp_AgregarFactura(303, 'Pendiente', '2024-06-03', 4, 198);
call sp_AgregarFactura(304, 'Pagada', '2024-06-04', 5, 199);
call sp_AgregarFactura(305, 'Pendiente', '2024-06-05', 6, 200);
call sp_AgregarFactura(306, 'Pagada', '2024-06-06', 7, 201);
call sp_AgregarFactura(307, 'Pendiente', '2024-06-07', 8, 202);
call sp_AgregarFactura(308, 'Pagada', '2024-06-08', 9, 203);
call sp_AgregarFactura(309, 'Pendiente', '2024-06-09', 10, 204);
call sp_AgregarFactura(310, 'Pagada', '2024-06-10', 11, 205);
call sp_AgregarFactura(311, 'Pendiente', '2024-06-11', 12, 206);
call sp_AgregarFactura(312, 'Pagada', '2024-06-12', 13, 207);
call sp_AgregarFactura(313, 'Pendiente', '2024-06-13', 14, 208);
call sp_AgregarFactura(314, 'Pagada', '2024-06-14', 15, 209);
call sp_AgregarFactura(315, 'Pendiente', '2024-06-15', 1, 195);
select * from clientes;
-- Listar
Delimiter $$
	create procedure sp_ListarFacturas()
		begin
			select 
				F.numeroFactura, 
                F.estado, 
                F.totalFactura,
                F.fechaFactura, 
                F.codigoCliente, 
                F.codigoEmpleado
			from Factura F;
		end$$
Delimiter ;

-- Buscar
Delimiter $$
	create procedure sp_BuscarFactura(in numFactura int)
		begin
			select 
				F.numeroFactura, 
                F.estado, 
                F.totalFactura,
                F.fechaFactura, 
                F.codigoCliente, 
                F.codigoEmpleado
			from Factura F
				where F.numeroFactura = numFactura;
		end$$
Delimiter ;

-- Elimiar
Delimiter $$
	create procedure sp_ElimiarFactura(in numFactu int)
		begin
			delete
				from Factura
					where Factura.numeroFactura = numFactu;
		end$$
Delimiter ;

-- Editar
Delimiter $$
	create procedure sp_EditarFactura(in numFc int, in estao varchar(50), in totalF decimal(10,2), in fehaFactura date)
		begin
			update  Factura F
				set F.estado = estao,
					F.totalFactura = totalF,
					F.fechaFactura = fehaFactura
				where F.numeroFactura = numFc;
		end$$
Delimiter ;

-- Procedimientos almacenados de la entidad: DetalleFactura

-- Agregar
Delimiter $$
	create Procedure sp_AgregarDetalleFactura(in cantidad int, in numeroFactura int, in codigoProducto varchar(15))
		Begin
			Insert into DetalleFactura (cantidad, numeroFactura, codigoProducto) values (cantidad, numeroFactura, codigoProducto);
        End$$
Delimiter ;

CALL sp_AgregarDetalleFactura(5, 301, '1AA');
CALL sp_AgregarDetalleFactura(3, 302, '2BB');
CALL sp_AgregarDetalleFactura(7, 303, '3CC');
CALL sp_AgregarDetalleFactura(2, 304, '4DD');
CALL sp_AgregarDetalleFactura(6, 305, '5EE');
CALL sp_AgregarDetalleFactura(4, 306, '6FF');
CALL sp_AgregarDetalleFactura(8, 307, '7GG');
CALL sp_AgregarDetalleFactura(1, 308, '8HH');
CALL sp_AgregarDetalleFactura(9, 309, '9II');
CALL sp_AgregarDetalleFactura(5, 310, '10JJ');
CALL sp_AgregarDetalleFactura(3, 311, '11KK');
CALL sp_AgregarDetalleFactura(7, 312, '12LL');
CALL sp_AgregarDetalleFactura(2, 313, '13MM');
CALL sp_AgregarDetalleFactura(6, 314, '14NN');
CALL sp_AgregarDetalleFactura(4, 315, '15OO');
CALL sp_AgregarDetalleFactura(5, 315, '9II');
CALL sp_AgregarDetalleFactura(10, 315, '7GG');
CALL sp_AgregarDetalleFactura(17, 315, '12LL');
CALL sp_AgregarDetalleFactura(5, 315, '13MM');

-- Listar
Delimiter $$
	create procedure sp_ListarDetallesFacturas()
		begin
			select 
				DF.codigoDetalleFactura, 
                DF.precioUnitario,
                DF.cantidad, 
                DF.numeroFactura, 
                DF.codigoProducto
			from DetalleFactura DF;
		end$$
Delimiter ;
-- Buscar
Delimiter $$
	create procedure sp_BuscarDetalleFactura(in codDetaleFactura int)
		begin
			select 
				DF.codigoDetalleFactura, 
                DF.precioUnitario, 
                DF.cantidad, 
                DF.numeroFactura, 
                DF.codigoProducto
			from DetalleFactura DF
				where DF.codigoDetalleFactura = codDetaleFactura;
		end$$
Delimiter ;

-- Elimiar
Delimiter $$
	create procedure sp_ElimiarDetalleFactura(in codDetaleFactur int)
		begin
			delete
				from DetalleFactura
					where DetalleFactura.codigoDetalleFactura = codDetaleFactur;
		end$$
Delimiter ;

-- Editar
Delimiter $$
	create procedure sp_EditarDetalleFactura(in codiDetaleFactur int, in precioU decimal(10,2), in cant int)
		begin
			update  DetalleFactura DF
				set 
					DF.precioUnitario = precioU,
					DF.cantidad = cant
				where DF.codigoDetalleFactura = codiDetaleFactur;
		end$$
Delimiter ;

-- Inerr's Join
-- Reporte Empleados
Delimiter $$
	create procedure sp_ListarEmpleadosCompleto()
	begin
		select
			E.codigoEmpleado, 
            E.nombresEmpleado, 
            E.apellidosEmpleado, 
            E.sueldo, 
            E.direccionEmpleado, 
            E.turno, 
            CE.nombreCargo
            from CargoEmpleado CE INNER JOIN Empleados E ON CE.codigoCargoEmpleado = E.codigoCargoEmpleado
            ORDER BY sueldo DESC;
		end$$
Delimiter ;

call sp_ListarEmpleadosCompleto();

-- Reporte Proveedor

Delimiter $$
	create procedure sp_ListarProuctoDelProveedor()
    begin
		select
			P.codigoProveedor,
			P.NITProveedor, 
            P.nombresProveedor, 
            P.apellidosProveedor, 
            P.direccionProveedor, 
            P.razonSocial, 
            P.contactoPrincipal, 
            P.paginaWeb,
            Pr.descripcionProducto
            From Productos Pr inner join Proveedores P on P.codigoProveedor = P.codigoProveedor;
    end$$
Delimiter ;

call sp_listarProuctoDelProveedor();

-- Reporte Factura

Delimiter $$
	create procedure sp_BuscarDatosFactura(in noFactura int)
    begin
		select
			F.numeroFactura,
            F.estado,
			F.totalFactura,
            F.fechaFactura,
            C.NITCliente,
            C.nombresCliente,
            C.apellidosCliente,
            C.direccionCliente,
            DF.precioUnitario,
            DF.cantidad,
            P.descripcionProducto,
            P.precioUnitario,
            E.nombresEmpleado,
            E.apellidosEmpleado,
            CE.nombreCargo
				from CargoEmpleado CE 
					inner join Empleados E on CE.codigoCargoEmpleado = E.codigoCargoEmpleado
					inner join Factura F on E.codigoEmpleado = F.codigoEmpleado
					inner join Clientes C on F.codigoCliente = C.codigoCliente
					inner join DetalleFactura DF on F.numeroFactura = DF.numeroFactura
					inner join Productos P on DF.codigoProducto = P.codigoProducto
						where F.numeroFactura = noFactura;
    end$$
Delimiter ;

call sp_BuscarDatosFactura(302);