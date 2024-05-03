package com.unifisweproject.hotelsupplymanagement.model.order;

public class Order {

    private int codice_ordine, codice_cliente;
    private final boolean bolla;      // bolla = true, fattura = false;
    private String tipo_pagamento, data_ordine;

    public Order(int codice_cliente, boolean bolla, String tipo_pagamento, String data_ordine) {

        this.codice_ordine = -1;
        this.codice_cliente = codice_cliente;
        this.bolla = bolla;
        this.tipo_pagamento = tipo_pagamento;
        this.data_ordine = data_ordine;

    }

    public Order(int codice_ordine, int codice_cliente, boolean bolla, String tipo_pagamento, String data_ordine) {

        this.codice_ordine = codice_ordine;
        this.codice_cliente = codice_cliente;
        this.bolla = bolla;
        this.tipo_pagamento = tipo_pagamento;
        this.data_ordine = data_ordine;

    }

    public int getCodice_ordine() {
        return codice_ordine;
    }

    public void setCodice_ordine(int codice_ordine) {
        this.codice_ordine = codice_ordine;
    }

    public int getCodice_cliente() {
        return codice_cliente;
    }

    public void setCodice_cliente(int codice_cliente) {
        this.codice_cliente = codice_cliente;
    }

    public boolean isBolla() {
        return bolla;
    }

    public String getTipo_pagamento() {
        return tipo_pagamento;
    }

    public void setTipo_pagamento(String tipo_pagamento) {
        this.tipo_pagamento = tipo_pagamento;
    }

    public String getData_ordine() {
        return data_ordine;
    }

    public void setData_ordine(String data_ordine) {
        this.data_ordine = data_ordine;
    }
}
