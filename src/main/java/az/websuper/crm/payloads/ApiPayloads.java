package az.websuper.crm.payloads;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiPayloads<T> {
    private boolean status;
    private  T data;
}
