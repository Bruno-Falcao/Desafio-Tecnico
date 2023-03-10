package com.attornatus.avaliacaodesenvolvedor.services;

import com.attornatus.avaliacaodesenvolvedor.controllers.form.PessoaForm;
import com.attornatus.avaliacaodesenvolvedor.exceptions.NotFoundException;
import com.attornatus.avaliacaodesenvolvedor.models.Endereco;
import com.attornatus.avaliacaodesenvolvedor.models.Pessoa;
import com.attornatus.avaliacaodesenvolvedor.repositories.PessoaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.attornatus.avaliacaodesenvolvedor.utils.PessoaUtils.buscaPessoaById;

@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;

    public PessoaService(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    /**
     * Caso o ID da Pessoa for informado, o método deve fazer a alteração da pessoa indicada.
     *
     * @param pessoa
     * @return
     */
    public String criacaoPessoa(Pessoa pessoa) throws Exception {
        if (pessoa.getId() == null) {
            Pessoa novaPessoa = pessoaRepository.save(pessoa);
            return "Nova pessoa criada com sucesso";
        }

        throw new Exception("Não foi possível salvar a pessoa");
    }

    /**
     * Deve receber um objeto com um ID válido e fazer a sua alteração
     *
     * @param pessoaForm
     * @return
     */
    public String alteraPessoa(PessoaForm pessoaForm) {
        Optional<Pessoa> pessoaExistente = buscaPessoaById(pessoaForm.getId(), pessoaRepository);

        Pessoa pessoa = pessoaForm.atualizar(pessoaForm.getId(), pessoaRepository);
        return "Pessoa alterada com sucesso";
    }

    public Pessoa buscaPorId(Long id) {
        return buscaPessoaById(id, pessoaRepository).get();
    }

    public List<Pessoa> buscaTodasAsPessoas() {
        List<Pessoa> pessoas = pessoaRepository.findAll();

        if (pessoas.isEmpty()) {
            throw new NotFoundException("Nenhuma pessoa encontrada");
        }
        return pessoas;
    }

    /**
     * O método retorna os endereços relacionados à pesosa
     *
     * @param id
     * @return
     */
    public List<Endereco> buscaPessoaEndereco(Long id) {
        Optional<Pessoa> pessoa = buscaPessoaById(id, pessoaRepository);
        return pessoa.map(Pessoa::getEndereco).orElse(null);
    }

    /**
     * O método define o endereço principal da pessoa, caso o endereço passado não esteja atribuído à pessoa,
     * o endereço principal se manterá o mesmo
     * @param id
     * @param idEndereco
     * @return
     */
    public String defineEnderecoPrincipal(Long id, Long idEndereco) throws Exception {
        Pessoa pessoa = buscaPessoaById(id, pessoaRepository).get();

        pessoa.getEndereco().forEach(c -> c.setEnderecoPrincipal(false));

        if (pessoa.getEndereco().stream().anyMatch(endereco -> endereco.getId().equals(idEndereco))) {
            pessoa.getEndereco().stream().filter(endereco -> endereco.getId()
                    .equals(idEndereco)).findFirst().ifPresent(endereco -> endereco.setEnderecoPrincipal(true));

            pessoaRepository.save(pessoa);
            return "Endereço principal alterado";
        }

        throw new Exception("Endereço informado não existe ou não pertence a essa Pessoa");
    }
}
