package com.jaax.edsa.modelo

import com.jaax.edsa.controlador.*
import dagger.Component
import javax.inject.Singleton

@Component(modules = [DBHModulo::class])
@Singleton
interface DBHComponent {
    fun inject(addCuentaFragment: AddCuentaFragment)
    fun inject(addMailFragment: AddMailFragment)
    fun inject(addUsuario: AddUsuario)
    fun inject(deleteCuentaFragment: DeleteCuentaFragment)
    fun inject(deleteMailFragment: DeleteMailFragment)
    fun inject(loginUsuario: LoginUsuario)
    fun inject(splashScreen: SplashScreen)
    fun inject(updateCuentaFragment: UpdateCuentaFragment)
    fun inject(updateMailFragment: UpdateMailFragment)
    fun inject(updateUsuario: UpdateUsuario)
    fun inject(verCuentas: VerCuentas)
    fun inject(verEmails: VerEmails)
}