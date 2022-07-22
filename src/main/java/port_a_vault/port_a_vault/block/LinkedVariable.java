package port_a_vault.port_a_vault.block;

public class LinkedVariable<T> {
    private T data;
    private boolean _isDeleted = false;

    public T getData(){
        if(_isDeleted){
            data = null;
        }
        return data;
    }

    public T setData(T value){
        return data = value;
    }

    public LinkedVariable(T input){
        data = input;
    }

    public void delete(){
        _isDeleted = true;
        data = null;
    }

    public boolean isDeleted(){
        return _isDeleted;
    }
}
