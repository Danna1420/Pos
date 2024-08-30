
drop table resources;

CREATE TABLE resources (
	`id` varchar(255) NOT NULL,
	`name` varchar(255) NOT NULL,
	`restype` int(11) NOT NULL,
	`content` mediumblob default NULL,
        `version` varchar(10) default NULL,
	PRIMARY KEY  ( `id` ),
	UNIQUE INDEX `resources_name_inx` ( `name` )
) ENGINE = InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT = Compact;

INSERT INTO resources(id, name, restype, content) VALUES('11', 'img.001', 1, $FILE{/com/openbravo/images/.01.png});
INSERT INTO resources(id, name, restype, content) VALUES('12', 'img.002', 1, $FILE{/com/openbravo/images/.02.png});
INSERT INTO resources(id, name, restype, content) VALUES('13', 'img.005', 1, $FILE{/com/openbravo/images/.05.png});
INSERT INTO resources(id, name, restype, content) VALUES('14', 'img.010', 1, $FILE{/com/openbravo/images/.10.png});
INSERT INTO resources(id, name, restype, content) VALUES('15', 'img.020', 1, $FILE{/com/openbravo/images/.20.png});
INSERT INTO resources(id, name, restype, content) VALUES('16', 'img.025', 1, $FILE{/com/openbravo/images/.25.png});
INSERT INTO resources(id, name, restype, content) VALUES('17', 'img.050', 1, $FILE{/com/openbravo/images/.50.png});
INSERT INTO resources(id, name, restype, content) VALUES('18', 'img.1', 1, $FILE{/com/openbravo/images/1.00.png});
INSERT INTO resources(id, name, restype, content) VALUES('19', 'img.2', 1, $FILE{/com/openbravo/images/2.00.png});
INSERT INTO resources(id, name, restype, content) VALUES('20', 'img.5', 1, $FILE{/com/openbravo/images/5.00.png});
INSERT INTO resources(id, name, restype, content) VALUES('21', 'img.10', 1, $FILE{/com/openbravo/images/10.00.png});
INSERT INTO resources(id, name, restype, content) VALUES('22', 'img.20', 1, $FILE{/com/openbravo/images/20.00.png});
INSERT INTO resources(id, name, restype, content) VALUES('23', 'img.50', 1, $FILE{/com/openbravo/images/50.00.png});
INSERT INTO resources(id, name, restype, content) VALUES('24', 'img.100', 1, $FILE{/com/openbravo/images/100.00.png});
INSERT INTO resources(id, name, restype, content) VALUES('25', 'img.200', 1, $FILE{/com/openbravo/images/200.00.png});
INSERT INTO resources(id, name, restype, content) VALUES('26', 'img.500', 1, $FILE{/com/openbravo/images/500.00.png});
INSERT INTO resources(id, name, restype, content) VALUES('28', 'img.cash', 1, $FILE{/com/openbravo/images/cash.png});
INSERT INTO resources(id, name, restype, content) VALUES('29', 'img.cashdrawer', 1, $FILE{/com/openbravo/images/cashdrawer.png});
INSERT INTO resources(id, name, restype, content) VALUES('30', 'img.discount', 1, $FILE{/com/openbravo/images/discount.png});
INSERT INTO resources(id, name, restype, content) VALUES('31', 'img.discount_b', 1, $FILE{/com/openbravo/images/discount_b.png});
INSERT INTO resources(id, name, restype, content) VALUES('32', 'img.heart', 1, $FILE{/com/openbravo/images/heart.png});
INSERT INTO resources(id, name, restype, content) VALUES('33', 'img.keyboard_48', 1, $FILE{/com/openbravo/images/keyboard_48.png});
INSERT INTO resources(id, name, restype, content) VALUES('34', 'img.kit_print', 1, $FILE{/com/openbravo/images/kit_print.png});
INSERT INTO resources(id, name, restype, content) VALUES('35', 'img.no_photo', 1, $FILE{/com/openbravo/images/no_photo.png});
INSERT INTO resources(id, name, restype, content) VALUES('36', 'img.refundit', 1, $FILE{/com/openbravo/images/refundit.png});
INSERT INTO resources(id, name, restype, content) VALUES('37', 'img.run_script', 1, $FILE{/com/openbravo/images/run_script.png});
INSERT INTO resources(id, name, restype, content) VALUES('38', 'img.ticket_print', 1, $FILE{/com/openbravo/images/ticket_print.png});
INSERT INTO resources(id, name, restype, content) VALUES('39', 'img.posapps', 1, $FILE{/com/openbravo/images/img.posapps.png});
INSERT INTO resources(id, name, restype, content) VALUES('40', 'Printer.Ticket.Logo', 1, $FILE{/com/openbravo/images/printer.ticket.logo.jpg});

-- PRINTER
INSERT INTO resources(id, name, restype, content) VALUES('41', 'Printer.CloseCash.Preview', 0, $FILE{/com/openbravo/pos/templates/Printer.CloseCash.Preview.xml});
INSERT INTO resources(id, name, restype, content) VALUES('42', 'Printer.CloseCash', 0, $FILE{/com/openbravo/pos/templates/Printer.CloseCash.xml});
INSERT INTO resources(id, name, restype, content) VALUES('43', 'Printer.CustomerPaid', 0, $FILE{/com/openbravo/pos/templates/Printer.CustomerPaid.xml});
INSERT INTO resources(id, name, restype, content) VALUES('44', 'Printer.CustomerPaid2', 0, $FILE{/com/openbravo/pos/templates/Printer.CustomerPaid2.xml});
INSERT INTO resources(id, name, restype, content) VALUES('45', 'Printer.FiscalTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.FiscalTicket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('46', 'Printer.Inventory', 0, $FILE{/com/openbravo/pos/templates/Printer.Inventory.xml});
INSERT INTO resources(id, name, restype, content) VALUES('47', 'Printer.OpenDrawer', 0, $FILE{/com/openbravo/pos/templates/Printer.OpenDrawer.xml});
INSERT INTO resources(id, name, restype, content) VALUES('48', 'Printer.PartialCash', 0, $FILE{/com/openbravo/pos/templates/Printer.PartialCash.xml});
INSERT INTO resources(id, name, restype, content) VALUES('49', 'Printer.PrintLastTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.PrintLastTicket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('50', 'Printer.Product', 0, $FILE{/com/openbravo/pos/templates/Printer.Product.xml});
INSERT INTO resources(id, name, restype, content) VALUES('52', 'Printer.ReprintTicket', 0, $FILE{/com/openbravo/pos/templates/Printer.ReprintTicket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('53', 'Printer.Start', 0, $FILE{/com/openbravo/pos/templates/Printer.Start.xml});
INSERT INTO resources(id, name, restype, content) VALUES('54', 'Printer.Ticket.P1', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P1.xml});
INSERT INTO resources(id, name, restype, content) VALUES('55', 'Printer.Ticket.P2', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P2.xml});
INSERT INTO resources(id, name, restype, content) VALUES('56', 'Printer.Ticket.P3', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P3.xml});
INSERT INTO resources(id, name, restype, content) VALUES('57', 'Printer.Ticket.P4', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P4.xml});
INSERT INTO resources(id, name, restype, content) VALUES('58', 'Printer.Ticket.P5', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P5.xml});
INSERT INTO resources(id, name, restype, content) VALUES('59', 'Printer.Ticket.P6', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.P6.xml});
INSERT INTO resources(id, name, restype, content) VALUES('60', 'Printer.Ticket', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket.xml});
INSERT INTO resources(id, name, restype, content) VALUES('61', 'Printer.Ticket2', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket2.xml});
INSERT INTO resources(id, name, restype, content) VALUES('62', 'Printer.TicketClose', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketClose.xml});
INSERT INTO resources(id, name, restype, content) VALUES('63', 'Printer.TicketLine', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketLine.xml});
INSERT INTO resources(id, name, restype, content) VALUES('64', 'Printer.TicketNew', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketLine.xml});
INSERT INTO resources(id, name, restype, content) VALUES('65', 'Printer.TicketPreview', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketPreview.xml});
INSERT INTO resources(id, name, restype, content) VALUES('66', 'Printer.Ticket_A4', 0, $FILE{/com/openbravo/pos/templates/Printer.Ticket_A4.xml});
INSERT INTO resources(id, name, restype, content) VALUES('67', 'Printer.TicketPreview_A4', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketPreview_A4.xml});
INSERT INTO resources(id, name, restype, content) VALUES('68', 'Printer.TicketRemote', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketRemote.xml});
INSERT INTO resources(id, name, restype, content) VALUES('69', 'Printer.TicketTotal', 0, $FILE{/com/openbravo/pos/templates/Printer.TicketTotal.xml});

-- SCRIPTS
INSERT INTO resources(id, name, restype, content) VALUES('70', 'script.Keyboard', 0, $FILE{/com/openbravo/pos/templates/script.Keyboard.txt});
INSERT INTO resources(id, name, restype, content) VALUES('71', 'script.Linediscount', 0, $FILE{/com/openbravo/pos/templates/script.Linediscount.txt});
INSERT INTO resources(id, name, restype, content) VALUES('72', 'script.posapps', 0, $FILE{/com/openbravo/pos/templates/script.posapps.txt});
INSERT INTO resources(id, name, restype, content) VALUES('73', 'script.SendOrder', 0, $FILE{/com/openbravo/pos/templates/script.SendOrder.txt});
INSERT INTO resources(id, name, restype, content) VALUES('74', 'script.Totaldiscount', 0, $FILE{/com/openbravo/pos/templates/script.Totaldiscount.txt});
INSERT INTO resources(id, name, restype, content) VALUES('75', 'script.multibuy', 0, $FILE{/com/openbravo/pos/templates/script.multibuy.txt});


INSERT IGNORE INTO resources(id, name, restype, content) VALUES('76', 'img.200', 1, $FILE{/com/openbravo/images/200.00.png});
INSERT IGNORE INTO resources(id, name, restype, content) VALUES('77', 'img.500', 1, $FILE{/com/openbravo/images/500.00.png});
INSERT IGNORE INTO resources(id, name, restype, content) VALUES('78', 'img.1000', 1, $FILE{/com/openbravo/images/1000.00.png});
INSERT IGNORE INTO resources(id, name, restype, content) VALUES('79', 'img.2000', 1, $FILE{/com/openbravo/images/2000.00.png});
INSERT IGNORE INTO resources(id, name, restype, content) VALUES('80', 'img.5000', 1, $FILE{/com/openbravo/images/5000.00.png});
INSERT IGNORE INTO resources(id, name, restype, content) VALUES('81', 'img.10000', 1, $FILE{/com/openbravo/images/10000.00.png});
INSERT IGNORE INTO resources(id, name, restype, content) VALUES('82', 'img.20000', 1, $FILE{/com/openbravo/images/20000.00.png});
INSERT IGNORE INTO resources(id, name, restype, content) VALUES('83', 'img.50000', 1, $FILE{/com/openbravo/images/50000.00.png});
INSERT IGNORE INTO resources(id, name, restype, content) VALUES('884', 'img.100000', 1, $FILE{/com/openbravo/images/100000.00.png});
